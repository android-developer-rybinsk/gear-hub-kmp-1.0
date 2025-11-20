package gearhub.feature.profile.navigation

import gear.hub.core.navigation.Destination

sealed class DestinationProfile(override val route: String) : Destination(route) {
    data object ProfileScreen : DestinationProfile("profile")
}