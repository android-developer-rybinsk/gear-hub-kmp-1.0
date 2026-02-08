package gearhub.feature.products.product_feature.internal.data

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.data.models.toDomain
import gearhub.feature.products.product_feature.internal.data.models.toDto
import gearhub.feature.products.product_feature.internal.domain.AdsRepository
import gearhub.feature.products.product_feature.internal.domain.models.AdDraft
import gearhub.feature.products.product_feature.internal.domain.models.CreateAdPayload
import gearhub.feature.products.product_feature.internal.domain.models.UpdateAdPayload
import gearhub.feature.products.product_service.api.AdsApi

class AdsRepositoryImpl(
    private val api: AdsApi,
) : AdsRepository {
    override suspend fun createAd(payload: CreateAdPayload): ApiResponse<AdDraft> {
        return when (val response = api.createAd(payload.toDto())) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomain())
            is ApiResponse.HttpError -> response
            ApiResponse.NetworkError -> ApiResponse.NetworkError
            is ApiResponse.UnknownError -> response
        }
    }

    override suspend fun updateAd(id: String, payload: UpdateAdPayload): ApiResponse<AdDraft> {
        return when (val response = api.updateAd(id, payload.toDto())) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomain())
            is ApiResponse.HttpError -> response
            ApiResponse.NetworkError -> ApiResponse.NetworkError
            is ApiResponse.UnknownError -> response
        }
    }
}
