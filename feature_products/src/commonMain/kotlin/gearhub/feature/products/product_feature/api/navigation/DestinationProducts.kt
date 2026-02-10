package gearhub.feature.products.product_feature.api.navigation

import gear.hub.core.navigation.Destination

sealed class DestinationProducts(override val route: String) : Destination(route) {
    data object MyProductsScreen : DestinationProducts("myProducts")
    data object CreateAdScreen : DestinationProducts("createAd")
}
