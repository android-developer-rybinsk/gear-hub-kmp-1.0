package gearhub.feature.products.navigation

import gear.hub.core.navigation.Destination

sealed class DestinationProducts(override val route: String) : Destination(route) {
    data object MyProductsScreen : DestinationProducts("myProducts")
}