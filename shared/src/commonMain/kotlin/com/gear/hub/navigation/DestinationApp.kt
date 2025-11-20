package com.gear.hub.navigation

import gear.hub.core.navigation.Destination

sealed class DestinationApp(override val route: String) : Destination(route) {
    data object SplashScreen : DestinationApp("splash")
    data object MainScreen : DestinationApp("main")
}