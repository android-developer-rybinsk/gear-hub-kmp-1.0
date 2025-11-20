package com.gear.hub.auth_service.di

import com.gear.hub.auth_service.api.AuthApi
import org.koin.dsl.module

/**
 * Koin-модуль для сетевого слоя авторизации.
 */
val authServiceModule = module {
    single<AuthApi> { provideAuthApi() }
}

/**
 * Платформенный способ получить реализацию AuthApi.
 */
expect fun provideAuthApi(): AuthApi
