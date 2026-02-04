package gearhub.feature.products.internal.data

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.internal.data.model.toDomain
import gearhub.feature.products.internal.data.model.toDto
import gearhub.feature.products.internal.domain.model.AdDraft
import gearhub.feature.products.internal.domain.model.CreateAdPayload
import gearhub.feature.products.internal.domain.model.UpdateAdPayload
import gearhub.feature.products.service.api.AdsApi

interface AdsRepository {
    suspend fun createAd(payload: CreateAdPayload): ApiResponse<AdDraft>

    suspend fun updateAd(id: String, payload: UpdateAdPayload): ApiResponse<AdDraft>
}

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
