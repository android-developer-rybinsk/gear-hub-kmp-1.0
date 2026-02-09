package com.gear.hub.network.di

import com.gear.hub.network.auth.AuthTokenProvider
import com.gear.hub.network.auth.AuthSessionManager
import com.gear.hub.network.client.NetworkClient
import com.gear.hub.network.client.NetworkClientQualifiers
import com.gear.hub.network.config.HostProvider
import com.gear.hub.network.util.ensureTrailingSlash
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit

private const val CONTENT_TYPE_HEADER = "Content-Type"
private const val CONTENT_TYPE_JSON = "application/json"
private const val RETRY_HEADER = "X-Auth-Retry"

/**
 * Платформенный модуль сети для Android: единый OkHttp + Retrofit c общими заголовками.
 */
actual fun platformNetworkModule(): Module = module {
    single<NetworkClient>(named(NetworkClientQualifiers.DEFAULT)) {
        provideRetrofit(get(), provideOkHttpClient(), get())
    }
    single<NetworkClient>(named(NetworkClientQualifiers.AUTHORIZED)) {
        provideRetrofit(get(), provideAuthorizedOkHttpClient(get(), get()), get())
    }
}

/**
 * Создаёт OkHttpClient с логированием и установкой Content-Type по умолчанию.
 */
private fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(defaultHeadersInterceptor())
    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    .build()

/**
 * Создаёт OkHttpClient с авторизацией (Bearer) поверх базовой конфигурации.
 */
private fun provideAuthorizedOkHttpClient(
    tokenProvider: AuthTokenProvider,
    sessionManager: AuthSessionManager,
): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(defaultHeadersInterceptor())
    .addInterceptor(authTokenInterceptor(tokenProvider, sessionManager))
    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    .build()

/**
 * Добавляет Content-Type заголовок ко всем запросам, если он не был указан явно.
 */
private fun defaultHeadersInterceptor(): Interceptor = Interceptor { chain ->
    val request = chain.request()
    val updatedRequest = if (request.header(CONTENT_TYPE_HEADER) == null) {
        request.newBuilder().addHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_JSON).build()
    } else {
        request
    }
    chain.proceed(updatedRequest)
}

/**
 * Добавляет Authorization заголовок для авторизованного клиента.
 */
private fun authTokenInterceptor(
    tokenProvider: AuthTokenProvider,
    sessionManager: AuthSessionManager,
): Interceptor = Interceptor { chain ->
    val request = chain.request()
    val token = tokenProvider.accessToken()?.takeIf { it.isNotBlank() }
    val updatedRequest = request.newBuilder()
        .apply {
            if (token != null) {
                addHeader("Authorization", "Bearer $token")
            }
        }
        .build()
    val response = chain.proceed(updatedRequest)
    if (response.code != 401 || request.header(RETRY_HEADER) != null) {
        return@Interceptor response
    }
    val refreshedToken = kotlinx.coroutines.runBlocking {
        sessionManager.refreshAccessToken()
    }
    if (refreshedToken.isNullOrBlank()) {
        kotlinx.coroutines.runBlocking { sessionManager.clearSession() }
        return@Interceptor response
    }
    response.close()
    val retryRequest = request.newBuilder()
        .removeHeader("Authorization")
        .addHeader("Authorization", "Bearer $refreshedToken")
        .addHeader(RETRY_HEADER, "true")
        .build()
    chain.proceed(retryRequest)
}

/**
 * Строит единый Retrofit для приложения.
 */
private fun provideRetrofit(
    hostProvider: HostProvider,
    okHttpClient: OkHttpClient,
    json: Json,
): Retrofit {
    val contentType = CONTENT_TYPE_JSON.toMediaType()
    return Retrofit.Builder()
        .baseUrl(hostProvider.baseUrl().ensureTrailingSlash())
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
}
