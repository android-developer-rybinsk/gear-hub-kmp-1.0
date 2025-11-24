package com.gear.hub.auth_service.di

import com.gear.hub.auth_service.api.AuthApi
import org.koin.dsl.module

/**
 * Koin-модуль для сетевого слоя авторизации.
 */
val authServiceModule = module {
    single<AuthApi> { provideAuthApi(get()) }
}

/**
 * Платформенный способ получить реализацию AuthApi, получая сетевой клиент из Koin.
 */
expect fun provideAuthApi(client: Any): AuthApi
