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
import androidx.navigation.navigation
import com.gear.hub.presentation.main.MainScreen
import com.gear.hub.presentation.models.TabItem
import gear.hub.core.navigation.Router
import gearhub.feature.chats.navigation.DestinationChats
import gearhub.feature.chats.presentation.chats.ChatsScreen
import gearhub.feature.chats.presentation.chats.ChatsViewModel
import gearhub.feature.menu.navigation.DestinationMenu
import gearhub.feature.menu.navigation.FilterArgs
import gearhub.feature.menu.navigation.ProductDetailsArgs
import gearhub.feature.menu.presentation.detail.ProductDetailsScreen
import gearhub.feature.menu.presentation.filter.FilterScreen
import gearhub.feature.menu.presentation.menu.MenuScreen
import gearhub.feature.menu.presentation.menu.MenuViewModel
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
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    val router: Router = getKoin().get { parametersOf(navController) }
    val state by viewModel.state.collectAsState()

    val onTabSelected: (TabItem) -> Unit = { tab ->
        if (currentRoute != tab.route) {
            navController.navigate(tab.route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = MAIN_GRAPH_ROUTE
    ) {
        navigation(
            startDestination = DestinationMenu.MenuScreen.route,
            route = MAIN_GRAPH_ROUTE
        ) {
            composable(DestinationMenu.MenuScreen.route) {
                val vm: MenuViewModel = koinViewModel(parameters = { parametersOf(router) })
                TabScaffold(
                    tabs = state.tabs,
                    currentRoute = currentRoute,
                    onTabSelected = onTabSelected
                ) { modifier ->
                    MenuScreen(vm, modifier = modifier)
                }
            }
            composable(DestinationProducts.MyProductsScreen.route) {
                val vm: MyProductsViewModel = koinViewModel(parameters = { parametersOf(router) })
                TabScaffold(
                    tabs = state.tabs,
                    currentRoute = currentRoute,
                    onTabSelected = onTabSelected
                ) { modifier ->
                    MyProductsScreen(vm, modifier = modifier)
                }
            }
            composable(DestinationChats.ChatsScreen.route) {
                val vm: ChatsViewModel = koinViewModel(parameters = { parametersOf(router) })
                TabScaffold(
                    tabs = state.tabs,
                    currentRoute = currentRoute,
                    onTabSelected = onTabSelected
                ) { modifier ->
                    ChatsScreen(vm, modifier = modifier)
                }
            }
            composable(DestinationProfile.ProfileScreen.route) {
                val vm: ProfileViewModel = koinViewModel(parameters = { parametersOf(rootRouter) })
                TabScaffold(
                    tabs = state.tabs,
                    currentRoute = currentRoute,
                    onTabSelected = onTabSelected
                ) { modifier ->
                    ProfileScreen(vm, modifier = modifier)
                }
            }
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
            FilterScreen(args = args) { navController.popBackStack() }
        }
        composable(
            route = DestinationMenu.DetailsScreen.ROUTE_PATTERN,
            arguments = listOf(
                navArgument(DestinationMenu.DetailsScreen.PRODUCT_ID_ARG) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString(DestinationMenu.DetailsScreen.PRODUCT_ID_ARG).orEmpty()
            val args = ProductDetailsArgs(productId = productId)
            ProductDetailsScreen(args = args) { navController.popBackStack() }
        }
    }
}

private const val MAIN_GRAPH_ROUTE = "main_graph"

@Composable
private fun TabScaffold(
    tabs: List<TabItem>,
    currentRoute: String?,
    onTabSelected: (TabItem) -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    MainScreen(
        tabs = tabs,
        currentRoute = currentRoute,
        onTabSelected = onTabSelected,
        content = content
    )
}
