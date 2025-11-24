package com.gear.hub

import com.gear.hub.di.initKoin
import gear.hub.core.navigation.Router
import com.gear.hub.navigation.RouterIOS
import com.gear.hub.presentation.screens.main.MainViewModel
import com.gear.hub.presentation.screens.splash.SplashViewModel
import com.gear.hub.auth_feature.internal.data.session.AuthSessionStorage
import com.gear.hub.auth_feature.internal.data.session.IosAuthSessionStorage
import gearhub.feature.chats.presentation.chats.ChatsViewModel
import gearhub.feature.menu.presentation.menu.MenuViewModel
import gearhub.feature.products.presentation.my.MyAdsViewModel
import gearhub.feature.profile.presentation.profile.ProfileViewModel
import org.koin.dsl.module

// Простая обёртка, которую Swift увидит как класс KoinIOSBridge
class KoinIOSBridge {
    fun doInit(router: RouterIOS) = initKoin(
        appDeclaration = {},
        iosModule = module {
            single<Router> { router }
            single<AuthSessionStorage> { IosAuthSessionStorage() }
            factory { MainViewModel(get()) }
            factory { SplashViewModel(get(), get()) }
            factory { MenuViewModel(get()) }
            factory { MyAdsViewModel(get()) }
            factory { ChatsViewModel(get()) }
            factory { ProfileViewModel(get()) }
        }
    )
}