package com.gear.hub

import com.gear.hub.di.initKoin
import gear.hub.core.navigation.Router
import com.gear.hub.navigation.RouterIOS
import com.gear.hub.presentation.screens.main.MainViewModel
import com.gear.hub.presentation.screens.splash.SplashViewModel
import com.gear.hub.auth_feature.api.session.AuthSessionDbDriver
import com.gear.hub.auth_feature.internal.data.session.AuthSessionStorage
import com.gear.hub.auth_feature.internal.data.session.AuthSessionStorageImpl
import com.gear.hub.auth_feature.api.session.createAuthSessionDbDriver
import gearhub.feature.chats.presentation.chats.ChatsViewModel
import gearhub.feature.menu.presentation.menu.MenuViewModel
import gearhub.feature.products.presentation.my.MyAdsViewModel
import gearhub.feature.profile.presentation.profile.ProfileViewModel
import com.gear.hub.data.config.DatabaseConfig
import com.gear.hub.data.config.PlatformContext
import com.gear.hub.data.di.dataModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.dsl.module

// Простая обёртка, которую Swift увидит как класс KoinIOSBridge
class KoinIOSBridge {
    private val initScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun doInit(router: RouterIOS) = initKoin(
        appDeclaration = {
            modules(
                dataModule(
                    config = DatabaseConfig(name = "gearhub_auth.db", passphrase = "gearhub_auth_cipher"),
                    platformContext = PlatformContext(null),
                    registryConfig = { registerModule("auth_session") { factory ->
                        initScope.launch { createAuthSessionDbDriver(factory).ensureInitialized() }
                    } },
                ),
            )
        },
        iosModule = module {
            single<Router> { router }
            single<AuthSessionDbDriver> { createAuthSessionDbDriver(get()) }
            single<AuthSessionStorage> { AuthSessionStorageImpl(get()) }
            factory { MainViewModel(get()) }
            factory { SplashViewModel(get(), get()) }
            factory { MenuViewModel(get()) }
            factory { MyAdsViewModel(get()) }
            factory { ChatsViewModel(get()) }
            factory { ProfileViewModel(get(), get(), get()) }
        }
    )
}