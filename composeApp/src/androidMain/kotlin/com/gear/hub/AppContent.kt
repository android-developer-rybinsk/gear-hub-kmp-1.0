package com.gear.hub

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gear.hub.auth_feature.api.AuthNavigationConfig
import com.gear.hub.auth_feature.internal.presentation.AuthScreen
import com.gear.hub.auth_feature.internal.presentation.AuthViewModel
import com.gear.hub.navigation.DestinationApp
import com.gear.hub.presentation.screens.main.MainScreenAndroid
import com.gear.hub.presentation.splash.SplashScreen
import com.gear.hub.ui.theme.GearHubTheme
import gear.hub.core.navigation.Router
import gear.hub.core.di.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun AppContent() {
    val navController = rememberNavController()
    val router: Router = getKoin().get { parametersOf(navController) }

    GearHubTheme {
        NavHost(
            navController = navController,
            startDestination = DestinationApp.SplashScreen.route
        ) {
            composable(DestinationApp.SplashScreen.route) {
                SplashScreen(getKoin().get { parametersOf(router) })
            }
            composable(DestinationApp.AuthScreen.route) {
                val config: AuthNavigationConfig = getKoin().get()
                val vm: AuthViewModel = koinViewModel { parametersOf(router, config) }
                AuthScreen(vm)
            }
            composable(DestinationApp.MainScreen.route) {
                MainScreenAndroid(
                    viewModel = getKoin().get { parametersOf(router) },
                    rootRouter = router,
                )
            }
        }
    }
}
