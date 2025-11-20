package com.gear.hub.navigation

import androidx.navigation.NavController
import gear.hub.core.navigation.Destination
import gear.hub.core.navigation.Router

class RouterAndroid(
    private val navController: NavController
) : Router {

    override fun navigate(destination: Destination) {
        navController.navigate(destination.route)
    }

    override fun back() {
        navController.popBackStack()
    }

    override fun popUpTo(destination: Destination, inclusive: Boolean) {
        navController.popBackStack(destination.route, inclusive)
    }

    override fun replaceAll(destination: Destination) {
        navController.navigate(destination.route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }
}