package com.gear.hub.di

import com.gear.hub.auth_feature.api.AuthFeatureApi
import gearhub.feature.menu_feature.api.MenuFeatureApi
import gearhub.feature.products.product_feature.internal.di.productFeatureModule
import com.gear.hub.auth_feature.api.AuthNavigationConfig
import com.gear.hub.navigation.DestinationApp
import com.gear.hub.network.config.Environment
import com.gear.hub.network.di.networkModule
import gearhub.feature.profile.api.ProfileNavigationConfig
import org.koin.dsl.module

/**
 * Базовый общий модуль Koin для всего приложения.
 */
val appModule = module {
    includes(
        networkModule(
            defaultEnv = Environment.DEV,
            devHost = "http://193.42.126.218:8000",
            prodHost = "https://prod.example.com",
        ),
        AuthFeatureApi.module,
        MenuFeatureApi.module,
        productFeatureModule,
    )

    single { AuthNavigationConfig(successDestination = DestinationApp.MainScreen) }
    single { ProfileNavigationConfig(logoutDestination = DestinationApp.AuthScreen) }
}
