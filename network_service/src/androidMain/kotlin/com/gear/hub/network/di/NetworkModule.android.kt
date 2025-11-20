package com.gear.hub.network.di

import com.gear.hub.network.config.HostProvider
import com.gear.hub.network.util.ensureTrailingSlash
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val CONTENT_TYPE_HEADER = "Content-Type"
private const val CONTENT_TYPE_JSON = "application/json"

/**
 * Платформенный модуль сети для Android: единый OkHttp + Retrofit c общими заголовками.
 */
actual fun platformNetworkModule(): Module = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get(), get(), get()) }
}

/**
 * Создаёт общий OkHttpClient с логированием и установкой Content-Type по умолчанию.
 */
private fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(defaultHeadersInterceptor())
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

