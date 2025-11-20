package com.gear.hub

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gear.hub.navigation.DestinationApp
import com.gear.hub.presentation.screens.main.MainScreenAndroid
import com.gear.hub.presentation.splash.SplashScreen
import gear.hub.core.navigation.Router
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun AppContent() {
    val navController = rememberNavController()
    val router: Router = getKoin().get { parametersOf(navController) }

    NavHost(
        navController = navController,
        startDestination = DestinationApp.SplashScreen.route
    ) {
        composable(DestinationApp.SplashScreen.route) {
            SplashScreen(getKoin().get { parametersOf(router) })
        }
        composable(DestinationApp.MainScreen.route) {
            MainScreenAndroid(getKoin().get { parametersOf(router) })
        }
    }
}