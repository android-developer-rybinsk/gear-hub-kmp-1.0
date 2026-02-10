package com.gear.hub.auth_service.di

import com.gear.hub.auth_service.api.AuthApi
import com.gear.hub.network.client.NetworkClient
import com.gear.hub.network.client.NetworkClientQualifiers
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin-модуль для сетевого слоя авторизации.
 */
val authServiceModule = module {
    single<AuthApi> {
        provideAuthApi(
            get(named(NetworkClientQualifiers.DEFAULT)),
            get(named(NetworkClientQualifiers.AUTHORIZED)),
            get(),
        )
    }
}

/**
 * Платформенный способ получить реализацию AuthApi, получая сетевой клиент из Koin.
 */
expect fun provideAuthApi(
    defaultClient: NetworkClient,
    authorizedClient: NetworkClient,
    hostProvider: com.gear.hub.network.config.HostProvider,
): AuthApi
