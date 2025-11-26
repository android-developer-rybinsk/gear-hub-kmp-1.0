package com.gear.hub.di

import androidx.navigation.NavController
import com.gear.hub.auth_feature.internal.domain.CheckAuthorizationUseCase
import com.gear.hub.navigation.RouterAndroid
import com.gear.hub.presentation.screens.main.MainViewModel
import com.gear.hub.presentation.screens.splash.SplashViewModel
import com.gear.hub.auth_feature.api.AuthNavigationConfig
import com.gear.hub.auth_feature.internal.presentation.AuthViewModel
import gear.hub.core.navigation.Router
import gearhub.feature.chats.presentation.chats.ChatsViewModel
import gearhub.feature.menu.presentation.menu.MenuViewModel
import gearhub.feature.products.presentation.my.MyAdsViewModel
import gearhub.feature.profile.api.ProfileNavigationConfig
import gearhub.feature.profile.presentation.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val androidModule = module {
    factory { (navController: NavController) -> RouterAndroid(navController) as Router }
    viewModel { (router: Router) -> MainViewModel(router) }
    viewModel { (router: Router) -> SplashViewModel(router, get<CheckAuthorizationUseCase>()) }
    viewModel { (router: Router, config: AuthNavigationConfig) -> AuthViewModel(get(), router, config) }
    viewModel { (router: Router) -> MenuViewModel(router) }
    viewModel { (router: Router) -> MyAdsViewModel(router) }
    viewModel { (router: Router) -> ChatsViewModel(router) }
    viewModel { (router: Router) -> ProfileViewModel(router, get(), get<ProfileNavigationConfig>()) }
}