package gearhub.feature.products.product_feature.internal.domain

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.domain.models.CreateAdPayloadDomain

class CreateAdDraftUseCase(
    private val repository: AdsRepository,
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        categoryId: Int,
    ): ApiResponse<String> {
        val payload = CreateAdPayloadDomain(
            title = title,
            description = description,
            categoryId = categoryId,
        )
        return when (val response = repository.createAd(payload)) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.id)
            is ApiResponse.HttpError -> response
            ApiResponse.NetworkError -> ApiResponse.NetworkError
            is ApiResponse.UnknownError -> response
        }
    }
}
