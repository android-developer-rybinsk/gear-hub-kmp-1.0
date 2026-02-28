package gearhub.feature.menu_feature.internal.presentation.menu

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.menu_feature.api.presentation.models.MenuProductUI
import gearhub.feature.products.product_feature.api.ProductAdsProvider

data class MenuProductsPage(
    val data: List<MenuProductUI>,
    val nextCursor: String?,
    val hasNextPage: Boolean,
)

class MenuDataProvider(
    private val productAdsProvider: ProductAdsProvider,
) {

    suspend fun products(limit: Int, cursor: String?): ApiResponse<MenuProductsPage> {
        return when (val response = productAdsProvider.getAds(limit = limit, cursor = cursor)) {
            is ApiResponse.Success -> {
                val page = response.data
                ApiResponse.Success(
                    MenuProductsPage(
                        data = page.data.map { ad ->
                            MenuProductUI(
                                id = ad.id,
                                title = ad.title?.takeIf { it.isNotBlank() } ?: "Без названия",
                                price = ad.price ?: 0.0,
                                imageUrl = ad.photoUrl,
                                categoryId = ad.categoryId.toString(),
                            )
                        },
                        nextCursor = page.nextCursor,
                        hasNextPage = page.hasNextPage,
                    )
                )
            }
            is ApiResponse.HttpError -> response
            ApiResponse.NetworkError -> ApiResponse.NetworkError
            is ApiResponse.UnknownError -> response
        }
    }
}
