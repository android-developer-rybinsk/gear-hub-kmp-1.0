package com.gear.hub

import android.app.Application
import com.gear.hub.di.androidModule
import com.gear.hub.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                appModule,      // общий модуль из shared
                androidModule   // Android-специфичный модуль
            )
        }
    }
}