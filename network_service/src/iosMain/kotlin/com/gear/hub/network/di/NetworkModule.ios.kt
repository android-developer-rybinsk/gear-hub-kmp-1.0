package com.gear.hub.network.di

import com.gear.hub.network.auth.AuthTokenProvider
import com.gear.hub.network.auth.AuthSessionManager
import com.gear.hub.network.client.NetworkClient
import com.gear.hub.network.client.NetworkClientQualifiers
import com.gear.hub.network.config.HostProvider
import com.gear.hub.network.util.ensureTrailingSlash
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.core.module.Module
import org.koin.dsl.module

private const val RETRY_HEADER = "X-Auth-Retry"

/**
 * Платформенный модуль сети для iOS: единый HttpClient c JSON сериализацией и базовым URL.
 */
actual fun platformNetworkModule(): Module = module {
    single<NetworkClient>(named(NetworkClientQualifiers.DEFAULT)) { provideIosHttpClient(get(), get()) }
    single<NetworkClient>(named(NetworkClientQualifiers.AUTHORIZED)) { provideAuthorizedIosHttpClient(get(), get(), get(), get()) }
}

/**
 * Создаёт HttpClient с базовым URL и обработкой JSON через kotlinx.serialization.
 */
private fun provideIosHttpClient(
    hostProvider: HostProvider,
    jsonSerializer: Json,
): HttpClient = HttpClient(Darwin) {
    install(ContentNegotiation) { json(jsonSerializer) }
    install(DefaultRequest) {
        url(hostProvider.baseUrl().ensureTrailingSlash())
        headers.append("Content-Type", "application/json")
    }
}

/**
 * Создаёт HttpClient с автоматическим добавлением Authorization заголовка.
 */
private fun provideAuthorizedIosHttpClient(
    hostProvider: HostProvider,
    jsonSerializer: Json,
    tokenProvider: AuthTokenProvider,
    sessionManager: AuthSessionManager,
): HttpClient = HttpClient(Darwin) {
    install(ContentNegotiation) { json(jsonSerializer) }
    install(DefaultRequest) {
        url(hostProvider.baseUrl().ensureTrailingSlash())
        headers.append("Content-Type", "application/json")
        tokenProvider.accessToken()?.takeIf { it.isNotBlank() }?.let { token ->
            headers.append("Authorization", "Bearer $token")
        }
    }
    install(HttpSend) {
        intercept { request ->
            if (request.headers[HttpHeaders.Authorization] == null && request.headers[RETRY_HEADER] == null) {
                val refreshed = sessionManager.refreshAccessToken()
                if (!refreshed.isNullOrBlank()) {
                    request.headers.append(HttpHeaders.Authorization, "Bearer $refreshed")
                }
            }
            val response = execute(request)
            if (response.status != HttpStatusCode.Unauthorized || request.headers[RETRY_HEADER] != null) {
                return@intercept response
            }
            val newToken = sessionManager.refreshAccessToken()
            if (newToken.isNullOrBlank()) {
                sessionManager.clearSession()
                return@intercept response
            }
            response.close()
            request.headers.remove(HttpHeaders.Authorization)
            request.headers.append(HttpHeaders.Authorization, "Bearer $newToken")
            request.headers.append(RETRY_HEADER, "true")
            execute(request)
        }
    }
}
