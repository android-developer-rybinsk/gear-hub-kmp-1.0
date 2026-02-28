package com.gear.hub

import com.gear.hub.di.initKoin
import gear.hub.core.navigation.Router
import com.gear.hub.navigation.RouterIOS
import com.gear.hub.navigation.DestinationApp
import com.gear.hub.presentation.screens.main.MainViewModel
import com.gear.hub.presentation.screens.splash.SplashViewModel
import com.gear.hub.auth_feature.api.AuthNavigationConfig
import com.gear.hub.auth_feature.api.session.AuthSessionDbDriver
import com.gear.hub.auth_feature.internal.data.session.AuthSessionStorage
import com.gear.hub.auth_feature.internal.data.session.AuthSessionStorageImpl
import com.gear.hub.auth_feature.api.session.createAuthSessionDbDriver
import com.gear.hub.auth_feature.internal.presentation.AuthViewModel
import gearhub.feature.menu_feature.api.db.createMenuCategoryDbDriver
import gearhub.feature.menu_feature.api.di.menuFeatureIosModule
import gearhub.feature.chats.presentation.chats.ChatsViewModel
import gearhub.feature.products.product_feature.internal.presentation.create.CreateAdViewModel
import gearhub.feature.products.product_feature.internal.presentation.my.MyProductsViewModel
import gearhub.feature.profile.presentation.profile.ProfileViewModel
import com.gear.hub.data.config.DatabaseConfig
import com.gear.hub.data.config.PlatformContext
import com.gear.hub.data.di.dataModule
import com.gear.hub.network.auth.SessionExpirationNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.qualifier.named
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
                    registryConfig = {
                        registerModule("auth_session") { factory ->
                            initScope.launch { createAuthSessionDbDriver(factory).ensureInitialized() }
                        }
                    },
                    qualifier = "auth_db",
                ),
                dataModule(
                    config = DatabaseConfig(name = "gearhub_menu.db", passphrase = "gearhub_menu_cipher"),
                    platformContext = PlatformContext(null),
                    registryConfig = {
                        registerModule("menu_categories") { factory ->
                            initScope.launch { createMenuCategoryDbDriver(factory).ensureInitialized() }
                        }
                    },
                    qualifier = "menu_db",
                ),
                menuFeatureIosModule,
            )
        },
        iosModule = module {
            single<Router> { router }
            single<AuthSessionDbDriver> { createAuthSessionDbDriver(get(named("auth_db"))) }
            single<AuthSessionStorage> { AuthSessionStorageImpl(get()) }
            factory { MainViewModel(get()) }
            factory { SplashViewModel(get(), get()) }
            factory { MyProductsViewModel(get()) }
            factory { CreateAdViewModel(get(), get(), get(), get()) }
            factory { ChatsViewModel(get()) }
            factory { ProfileViewModel(get(), get(), get()) }
            factory { AuthViewModel(get(), get(), get(), get<AuthNavigationConfig>()) }
        }
    ).also { koin ->
        val notifier = koin.get<SessionExpirationNotifier>()
        initScope.launch {
            notifier.events.collect {
                router.replaceAll(DestinationApp.AuthScreen)
            }
        }
    }
}
