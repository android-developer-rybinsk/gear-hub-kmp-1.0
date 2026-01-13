package com.gear.hub.network.di

import com.gear.hub.network.config.Environment
import com.gear.hub.network.config.HostProvider
import com.gear.hub.network.config.PlatformHostProvider
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Базовый модуль сети: провайдер хоста, общий Json и платформенные реализации клиентов.
 */
fun networkModule(
    defaultEnv: Environment = Environment.DEV,
    devHost: String,
    prodHost: String,
): Module = module {
    single<HostProvider> { PlatformHostProvider(defaultEnv, devHost, prodHost) }
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }

    includes(platformNetworkModule())
}

/**
 * Платформенно-специфичный набор зависимостей сети (Retrofit/OkHttp для Android, HttpClient для iOS).
 */
expect fun platformNetworkModule(): Module
