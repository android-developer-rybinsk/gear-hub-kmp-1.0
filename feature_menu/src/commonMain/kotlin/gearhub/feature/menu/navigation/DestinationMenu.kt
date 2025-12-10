package gearhub.feature.menu.navigation

import gear.hub.core.navigation.Destination

data class FilterArgs(val categoryId: String? = null)

data class ProductDetailsArgs(val productId: String)

sealed class DestinationMenu(override val route: String) : Destination(route) {
    data object MenuScreen : DestinationMenu("menu")

    data class FilterScreen(val args: FilterArgs = FilterArgs()) : DestinationMenu(buildRoute(args)) {
        companion object {
            const val CATEGORY_ARG = "categoryId"
            const val ROUTE_PATTERN = "menu/filter?${CATEGORY_ARG}={${CATEGORY_ARG}}"

            fun buildRoute(args: FilterArgs): String =
                "menu/filter?$CATEGORY_ARG=${args.categoryId.orEmpty()}"
        }
    }

    data class DetailsScreen(val args: ProductDetailsArgs) : DestinationMenu("menu/details/${args.productId}") {
        companion object {
            const val PRODUCT_ID_ARG = "productId"
            const val ROUTE_PATTERN = "menu/details/{$PRODUCT_ID_ARG}"
        }
    }
}
