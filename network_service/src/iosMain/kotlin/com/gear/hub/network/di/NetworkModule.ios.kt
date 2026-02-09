package com.gear.hub.network.di

import com.gear.hub.network.auth.AuthTokenProvider
import com.gear.hub.network.client.NetworkClient
import com.gear.hub.network.client.NetworkClientQualifiers
import com.gear.hub.network.config.HostProvider
import com.gear.hub.network.util.ensureTrailingSlash
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Платформенный модуль сети для iOS: единый HttpClient c JSON сериализацией и базовым URL.
 */
actual fun platformNetworkModule(): Module = module {
    single<NetworkClient>(named(NetworkClientQualifiers.DEFAULT)) { provideIosHttpClient(get(), get()) }
    single<NetworkClient>(named(NetworkClientQualifiers.AUTHORIZED)) { provideAuthorizedIosHttpClient(get(), get(), get()) }
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
): HttpClient = HttpClient(Darwin) {
    install(ContentNegotiation) { json(jsonSerializer) }
    install(DefaultRequest) {
        url(hostProvider.baseUrl().ensureTrailingSlash())
        headers.append("Content-Type", "application/json")
        tokenProvider.accessToken()?.takeIf { it.isNotBlank() }?.let { token ->
            headers.append("Authorization", "Bearer $token")
        }
    }
}
