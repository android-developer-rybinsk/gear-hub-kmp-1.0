package gear.hub.core.navigation

interface Router {
    fun navigate(destination: Destination)
    fun back()
    fun popUpTo(destination: Destination, inclusive: Boolean)
    fun replaceAll(destination: Destination)
}