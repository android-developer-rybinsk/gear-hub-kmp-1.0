package com.gear.hub

import android.app.Application
import com.gear.hub.di.androidModule
import com.gear.hub.di.appModule
import com.gear.hub.data.config.DatabaseConfig
import com.gear.hub.data.config.PlatformContext
import com.gear.hub.data.di.dataModule
import com.gear.hub.auth_feature.api.session.createAuthSessionDbDriver
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                dataModule(
                    config = DatabaseConfig(name = "gearhub_auth.db", passphrase = "gearhub_auth_cipher"),
                    platformContext = PlatformContext(this@App),
                    registryConfig = { registerModule("auth_session") { factory -> createAuthSessionDbDriver(factory).ensureInitialized() } },
                ),
                appModule,      // общий модуль из shared
                androidModule   // Android-специфичный модуль
            )
        }
    }
}