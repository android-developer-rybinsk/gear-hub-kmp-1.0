package gearhub.feature.products.product_feature.api

import com.gear.hub.network.model.ApiResponse

data class ProductAdsPageModel(
    val data: List<ProductAdModel>,
    val limit: Int,
    val nextCursor: String?,
    val hasNextPage: Boolean,
)

data class ProductAdModel(
    val id: String,
    val title: String?,
    val price: Double?,
    val categoryId: Int,
    val photoUrl: String?,
)

interface ProductAdsProvider {
    suspend fun getAds(limit: Int = 20, cursor: String? = null): ApiResponse<ProductAdsPageModel>
}
