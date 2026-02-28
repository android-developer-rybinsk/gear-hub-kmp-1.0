package com.gear.hub.auth_feature.internal.di

import com.gear.hub.auth_feature.internal.data.AuthRepositoryImpl
import com.gear.hub.auth_feature.internal.data.AuthSessionManagerImpl
import com.gear.hub.auth_feature.internal.data.AuthSessionRepositoryImpl
import com.gear.hub.auth_feature.internal.data.AuthTokenProviderImpl
import com.gear.hub.auth_feature.api.session.AuthSessionDbDriver
import com.gear.hub.auth_feature.internal.data.session.AuthSessionStorage
import com.gear.hub.auth_feature.internal.data.session.AuthSessionStorageImpl
import com.gear.hub.auth_feature.api.session.createAuthSessionDbDriver
import com.gear.hub.auth_feature.internal.domain.AuthRepository
import com.gear.hub.auth_feature.internal.domain.AuthSessionRepository
import com.gear.hub.network.auth.AuthSessionManager
import com.gear.hub.network.auth.AuthTokenProvider
import com.gear.hub.auth_feature.internal.domain.CheckAuthorizationUseCase
import com.gear.hub.auth_feature.internal.domain.LogoutUseCase
import com.gear.hub.auth_feature.internal.domain.LoginUserUseCase
import com.gear.hub.auth_feature.internal.domain.RegisterUserUseCase
import com.gear.hub.data.config.DatabaseFactory
import com.gear.hub.auth_service.di.authServiceModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Внутренний модуль Koin для фичи авторизации.
 */
val authFeatureModule: Module = module {
    includes(authServiceModule)

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<AuthSessionDbDriver> { createAuthSessionDbDriver(get<DatabaseFactory>(named("auth_db"))) }
    single<AuthSessionStorage> { AuthSessionStorageImpl(get()) }
    single<AuthSessionRepository> { AuthSessionRepositoryImpl(get()) }
    single<AuthTokenProvider> { AuthTokenProviderImpl(get()) }
    single<AuthSessionManager> { AuthSessionManagerImpl(get(), get(), get()) }
    factory { RegisterUserUseCase(get(), get()) }
    factory { LoginUserUseCase(get(), get()) }
    factory { CheckAuthorizationUseCase(get()) }
    factory { LogoutUseCase(get()) }

    scope<AuthFeatureScope> {
        scoped { Dispatchers.IO }
        scoped { SupervisorJob() }
    }
}

/**
 * Точка определения scope для зависимостей фичи авторизации.
 */
object AuthFeatureScope
