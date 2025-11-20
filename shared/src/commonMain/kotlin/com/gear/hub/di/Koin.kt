package com.gear.hub.di

import com.gear.hub.presentation.screens.main.MainViewModel
import com.gear.hub.presentation.screens.splash.SplashViewModel
import gearhub.feature.chats.presentation.chats.ChatsViewModel
import gearhub.feature.menu.presentation.menu.MenuViewModel
import gearhub.feature.products.presentation.my.MyAdsViewModel
import gearhub.feature.profile.presentation.profile.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

private lateinit var koinApp: KoinApplication

fun initKoin(appDeclaration: KoinAppDeclaration = {}, iosModule: Module): Koin {
    koinApp = startKoin {
        appDeclaration()
        modules(appModule + iosModule)
    }
    return koinApp.koin
}

fun getKoin(): Koin = koinApp.koin
fun mainScope(): CoroutineScope = kotlinx.coroutines.MainScope()


fun resolveSplashVM(): SplashViewModel = getKoin().get()
fun resolveMainVM(): MainViewModel = getKoin().get()
fun resolveMenuVM(): MenuViewModel = getKoin().get()
fun resolveMyAdsVM(): MyAdsViewModel = getKoin().get()
fun resolveChatsVM(): ChatsViewModel = getKoin().get()
fun resolveProfileVM(): ProfileViewModel = getKoin().get()