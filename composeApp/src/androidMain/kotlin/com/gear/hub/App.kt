package com.gear.hub

import android.app.Application
import com.gear.hub.di.androidModule
import com.gear.hub.di.appModule
import com.gear.hub.data.config.DatabaseConfig
import com.gear.hub.data.config.PlatformContext
import com.gear.hub.data.di.dataModule
import com.gear.hub.auth_feature.api.session.createAuthSessionDbDriver
import gearhub.feature.menu_feature.api.db.createMenuCategoryDbDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                dataModule(
                    config = DatabaseConfig(name = "gearhub_auth.db", passphrase = "gearhub_auth_cipher"),
                    platformContext = PlatformContext(this@App),
                    registryConfig = {
                        registerModule("auth_session") { factory ->
                            appScope.launch { createAuthSessionDbDriver(factory).ensureInitialized() }
                        }
                        registerModule("menu_categories") { factory ->
                            appScope.launch { createMenuCategoryDbDriver(factory).ensureInitialized() }
                        }
                    },
                ),
                appModule,      // общий модуль из shared
                androidModule   // Android-специфичный модуль
            )
        }
    }
}
