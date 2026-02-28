package gearhub.feature.products.product_feature.internal.data

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.api.ProductAdModel
import gearhub.feature.products.product_feature.api.ProductAdsPageModel
import gearhub.feature.products.product_feature.api.ProductAdsProvider
import gearhub.feature.products.product_feature.internal.domain.AdsRepository

internal class ProductAdsProviderImpl(
    private val repository: AdsRepository,
) : ProductAdsProvider {
    override suspend fun getAds(limit: Int, cursor: String?): ApiResponse<ProductAdsPageModel> {
        return when (val response = repository.getAds(limit = limit, cursor = cursor)) {
            is ApiResponse.Success -> {
                val page = response.data
                ApiResponse.Success(
                    ProductAdsPageModel(
                        data = page.data.map {
                            ProductAdModel(
                                id = it.id,
                                title = it.title,
                                price = it.price,
                                categoryId = it.categoryId,
                                photoUrl = it.photos.minByOrNull { photo -> photo.sortOrder }?.url,
                            )
                        },
                        limit = page.pageInfo.limit,
                        nextCursor = page.pageInfo.nextCursor,
                        hasNextPage = page.pageInfo.hasNextPage,
                    )
                )
            }
            is ApiResponse.HttpError -> response
            ApiResponse.NetworkError -> ApiResponse.NetworkError
            is ApiResponse.UnknownError -> response
        }
    }
}
