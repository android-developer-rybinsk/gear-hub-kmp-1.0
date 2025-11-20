package gear.hub.core.navigation

sealed class NavigationAction {
    data class Navigate(val destination: Destination) : NavigationAction()
    object Back : NavigationAction()
    data class PopUpTo(val destination: Destination, val inclusive: Boolean) : NavigationAction()
    data class ReplaceAll(val destination: Destination) : NavigationAction()
}