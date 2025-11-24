package com.gear.hub.auth_service.di

import com.gear.hub.auth_service.api.AuthApi
import com.gear.hub.network.client.NetworkClient
import org.koin.dsl.module

/**
 * Koin-модуль для сетевого слоя авторизации.
 */
val authServiceModule = module {
    single<AuthApi> { provideAuthApi(get<NetworkClient>(), get()) }
}

/**
 * Платформенный способ получить реализацию AuthApi, получая сетевой клиент из Koin.
 */
expect fun provideAuthApi(client: NetworkClient, hostProvider: com.gear.hub.network.config.HostProvider): AuthApi
