package gearhub.feature.menu.navigation

import gear.hub.core.navigation.Destination

sealed class DestinationMenu(override val route: String) : Destination(route) {
    data object MenuScreen : DestinationMenu("menu")
}