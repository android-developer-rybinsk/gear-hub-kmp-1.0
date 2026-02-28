package gearhub.feature.products.product_feature.internal.data

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.api.ProductCategoryInfoModel
import gearhub.feature.products.product_feature.api.ProductCategoryProvider
import gearhub.feature.products.product_service.api.AdsApi

internal class ProductCategoryProviderImpl(
    private val adsApi: AdsApi,
) : ProductCategoryProvider {
    override suspend fun getCategories(): List<ProductCategoryInfoModel> {
        return when (val response = adsApi.getCategories()) {
            is ApiResponse.Success -> response.data.map { category ->
                ProductCategoryInfoModel(
                    id = category.id.toString(),
                    title = category.title,
                    slug = category.slug,
                )
            }
            is ApiResponse.HttpError -> emptyList()
            ApiResponse.NetworkError -> emptyList()
            is ApiResponse.UnknownError -> emptyList()
        }
    }
}
