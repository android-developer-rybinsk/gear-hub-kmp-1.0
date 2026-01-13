package com.gear.hub.presentation.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gear.hub.presentation.main.MainScreen
import com.gear.hub.presentation.models.TabItem
import gear.hub.core.navigation.Router
import gearhub.feature.chats.navigation.DestinationChats
import gearhub.feature.chats.presentation.chats.ChatsScreen
import gearhub.feature.chats.presentation.chats.ChatsViewModel
import gearhub.feature.menu.navigation.DestinationMenu
import gearhub.feature.menu.navigation.FilterArgs
import gearhub.feature.menu.navigation.ProductDetailsArgs
import gearhub.feature.menu.navigation.SearchArgs
import gearhub.feature.menu.presentation.detail.ProductDetailsScreen
import gearhub.feature.menu.presentation.filter.FilterScreen
import gearhub.feature.menu.presentation.menu.MenuScreen
import gearhub.feature.menu.presentation.menu.MenuViewModel
import gearhub.feature.menu.presentation.search.SearchResultsScreen
import gearhub.feature.menu.presentation.search.SearchResultsViewModel
import gearhub.feature.products.MyProductsScreen
import gearhub.feature.products.navigation.DestinationProducts
import gearhub.feature.products.presentation.my.MyProductsViewModel
import gearhub.feature.profile.navigation.DestinationProfile
import gearhub.feature.profile.presentation.profile.ProfileScreen
import gearhub.feature.profile.presentation.profile.ProfileViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun MainScreenAndroid(viewModel: MainViewModel, rootRouter: Router) {
    val rootNavController = rememberNavController()
    val router: Router = getKoin().get { parametersOf(rootNavController) }
    val state by viewModel.state.collectAsState()
    NavHost(
        navController = rootNavController,
        startDestination = MAIN_TABS_ROUTE
    ) {
        composable(MAIN_TABS_ROUTE) {
            TabsHost(
                tabs = state.tabs,
                router = router,
                rootRouter = rootRouter
            )
        }
        composable(
            route = DestinationMenu.FilterScreen.ROUTE_PATTERN,
            arguments = listOf(
                navArgument(DestinationMenu.FilterScreen.CATEGORY_ARG) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments
                ?.getString(DestinationMenu.FilterScreen.CATEGORY_ARG)
                ?.takeIf { it.isNotBlank() }
            val args = FilterArgs(categoryId = categoryId)
            FilterScreen(args = args) { rootNavController.popBackStack() }
        }
        composable(
            route = DestinationMenu.DetailsScreen.ROUTE_PATTERN,
            arguments = listOf(
                navArgument(DestinationMenu.DetailsScreen.PRODUCT_ID_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString(DestinationMenu.DetailsScreen.PRODUCT_ID_ARG).orEmpty()
            val args = ProductDetailsArgs(productId = productId)
            ProductDetailsScreen(args = args) { rootNavController.popBackStack() }
        }
        composable(
            route = DestinationMenu.SearchResultsScreen.ROUTE_PATTERN,
            arguments = listOf(
                navArgument(DestinationMenu.SearchResultsScreen.QUERY_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString(DestinationMenu.SearchResultsScreen.QUERY_ARG).orEmpty()
            val args = SearchArgs(query = query)
            val vm: SearchResultsViewModel = koinViewModel(parameters = { parametersOf(router, query) })
            SearchResultsScreen(args = args, viewModel = vm)
        }
    }
}

private const val MAIN_TABS_ROUTE = "main_tabs"

@Composable
private fun TabsHost(
    tabs: List<TabItem>,
    router: Router,
    rootRouter: Router
) {
    val tabNavController = rememberNavController()
    val tabBackStackEntry = tabNavController.currentBackStackEntryAsState()
    val currentRoute = tabBackStackEntry.value?.destination?.route

    val onTabSelected: (TabItem) -> Unit = { tab ->
        if (currentRoute != tab.route) {
            tabNavController.navigate(tab.route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(tabNavController.graph.findStartDestination().id) { saveState = true }
            }
        }
    }

    MainScreen(
        tabs = tabs,
        currentRoute = currentRoute,
        onTabSelected = onTabSelected
    ) { innerModifier ->
        NavHost(
            navController = tabNavController,
            startDestination = DestinationMenu.MenuScreen.route,
            modifier = innerModifier
        ) {
            composable(DestinationMenu.MenuScreen.route) {
                val vm: MenuViewModel = koinViewModel(parameters = { parametersOf(router) })
                MenuScreen(vm)
            }
            composable(DestinationProducts.MyProductsScreen.route) {
                val vm: MyProductsViewModel = koinViewModel(parameters = { parametersOf(router) })
                MyProductsScreen(vm)
            }
            composable(DestinationChats.ChatsScreen.route) {
                val vm: ChatsViewModel = koinViewModel(parameters = { parametersOf(router) })
                ChatsScreen(vm)
            }
            composable(DestinationProfile.ProfileScreen.route) {
                val vm: ProfileViewModel = koinViewModel(parameters = { parametersOf(rootRouter) })
                ProfileScreen(vm)
            }
        }
    }
}
