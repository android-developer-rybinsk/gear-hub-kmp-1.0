package gearhub.feature.products.product_feature.internal.domain

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.domain.models.UpdateAdPayloadDomain

class UpdateAdDraftUseCase(
    private val repository: AdsRepository,
) {
    suspend operator fun invoke(
        adId: String,
        price: String,
    ): ApiResponse<Unit> {
        val payload = UpdateAdPayloadDomain(price = price)
        return when (val response = repository.updateAd(adId, payload)) {
            is ApiResponse.Success -> ApiResponse.Success(Unit)
            is ApiResponse.HttpError -> response
            ApiResponse.NetworkError -> ApiResponse.NetworkError
            is ApiResponse.UnknownError -> response
        }
    }
}
