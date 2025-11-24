package com.gear.hub.auth_feature.internal.di

import com.gear.hub.auth_feature.api.AuthNavigationConfig
import com.gear.hub.auth_feature.internal.data.AuthRepositoryImpl
import com.gear.hub.auth_feature.internal.domain.AuthRepository
import com.gear.hub.auth_feature.internal.domain.RegisterUserUseCase
import com.gear.hub.auth_feature.internal.presentation.AuthViewModel
import com.gear.hub.auth_service.di.authServiceModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Внутренний модуль Koin для фичи авторизации.
 */
val authFeatureModule: Module = module {
    includes(authServiceModule)

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    factory { RegisterUserUseCase(get()) }

    scope<AuthFeatureScope> {
        scoped { Dispatchers.IO }
        scoped { SupervisorJob() }
    }

    factory { (config: AuthNavigationConfig) ->
        AuthViewModel(
            registerUserUseCase = get(),
            router = get(),
            navigationConfig = config,
        )
    }
}

/**
 * Точка определения scope для зависимостей фичи авторизации.
 */
object AuthFeatureScope
