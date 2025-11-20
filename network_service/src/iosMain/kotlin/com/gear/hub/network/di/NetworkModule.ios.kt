package com.gear.hub.network.di

import com.gear.hub.network.config.HostProvider
import com.gear.hub.network.util.ensureTrailingSlash
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Платформенный модуль сети для iOS: единый HttpClient c JSON сериализацией и базовым URL.
 */
actual fun platformNetworkModule(): Module = module {
    single { provideIosHttpClient(get()) }
}

/**
 * Создаёт HttpClient с базовым URL и обработкой JSON через kotlinx.serialization.
 */
private fun provideIosHttpClient(hostProvider: HostProvider): HttpClient = HttpClient(Darwin) {
    install(ContentNegotiation) { json() }
    install(DefaultRequest) {
        url(hostProvider.baseUrl().ensureTrailingSlash())
        headers.append("Content-Type", "application/json")
    }
}
