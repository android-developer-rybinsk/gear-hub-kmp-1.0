package gearhub.feature.menu.navigation

import gear.hub.core.navigation.Destination

sealed class DestinationMenu(override val route: String) : Destination(route) {
    data object MenuScreen : DestinationMenu("menu")

    data class FilterScreen(val categoryId: String? = null) : DestinationMenu(buildRoute(categoryId)) {
        companion object {
            const val CATEGORY_ARG = "categoryId"
            const val ROUTE_PATTERN = "menu/filter?${CATEGORY_ARG}={${CATEGORY_ARG}}"

            fun buildRoute(categoryId: String?): String =
                "menu/filter?$CATEGORY_ARG=${categoryId.orEmpty()}"
        }
    }

    data class DetailsScreen(val productId: String) : DestinationMenu("menu/details/$productId") {
        companion object {
            const val PRODUCT_ID_ARG = "productId"
            const val ROUTE_PATTERN = "menu/details/{$PRODUCT_ID_ARG}"
        }
    }
}