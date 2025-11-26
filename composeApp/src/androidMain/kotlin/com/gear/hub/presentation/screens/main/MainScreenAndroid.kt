package com.gear.hub.presentation.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gear.hub.presentation.main.MainScreen
import gear.hub.core.navigation.Router
import gearhub.feature.chats.navigation.DestinationChats
import gearhub.feature.chats.presentation.chats.ChatsScreen
import gearhub.feature.chats.presentation.chats.ChatsViewModel
import gearhub.feature.menu.navigation.DestinationMenu
import gearhub.feature.menu.presentation.menu.MenuScreen
import gearhub.feature.menu.presentation.menu.MenuViewModel
import gearhub.feature.products.MyAdsScreen
import gearhub.feature.products.navigation.DestinationProducts
import gearhub.feature.products.presentation.my.MyAdsViewModel
import gearhub.feature.profile.navigation.DestinationProfile
import gearhub.feature.profile.presentation.profile.ProfileScreen
import gearhub.feature.profile.presentation.profile.ProfileViewModel
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun MainScreenAndroid(viewModel: MainViewModel, rootRouter: Router) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    val router: Router = getKoin().get { parametersOf(navController) }
    val state by viewModel.state.collectAsState()

    MainScreen(
        tabs = state.tabs,
        currentRoute = currentRoute,
        onTabSelected = { tab ->
            if (currentRoute != tab.route) {
                navController.navigate(tab.route) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = DestinationMenu.MenuScreen.route,
        modifier = Modifier
    ) {
        composable(DestinationMenu.MenuScreen.route) {
            val vm: MenuViewModel = getKoin().get { parametersOf(router) }
            MenuScreen(vm)
        }
        composable(DestinationProducts.MyAdsScreen.route) {
            val vm: MyAdsViewModel = getKoin().get { parametersOf(router) }
            MyAdsScreen(vm)
        }
        composable(DestinationChats.ChatsScreen.route) {
            val vm: ChatsViewModel = getKoin().get { parametersOf(router) }
            ChatsScreen(vm)
        }
        composable(DestinationProfile.ProfileScreen.route) {
            val vm: ProfileViewModel = getKoin().get { parametersOf(rootRouter) }
            ProfileScreen(vm)
        }
    }
}