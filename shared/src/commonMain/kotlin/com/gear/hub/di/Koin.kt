package com.gear.hub.di

import com.gear.hub.auth_feature.internal.presentation.AuthViewModel
import com.gear.hub.presentation.screens.main.MainViewModel
import com.gear.hub.presentation.screens.splash.SplashViewModel
import gearhub.feature.chats.presentation.chats.ChatsViewModel
import gearhub.feature.menu_feature.api.MenuViewModelApi
import gearhub.feature.products.product_feature.internal.presentation.my.MyProductsViewModel
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
fun resolveMenuVM(): MenuViewModelApi = getKoin().get()
fun resolveMyProductsVM(): MyProductsViewModel = getKoin().get()
fun resolveChatsVM(): ChatsViewModel = getKoin().get()
fun resolveProfileVM(): ProfileViewModel = getKoin().get()
fun resolveAuthVM(): AuthViewModel = getKoin().get()
