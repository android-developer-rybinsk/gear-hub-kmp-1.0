package gearhub.feature.products.product_feature.internal.data

import com.gear.hub.network.model.ApiResponse
import com.gear.hub.auth_feature.api.session.AuthSessionDbDriver
import gearhub.feature.products.product_feature.internal.data.models.toDomain
import gearhub.feature.products.product_feature.internal.data.models.toData
import gearhub.feature.products.product_feature.internal.domain.AdsRepository
import gearhub.feature.products.product_feature.internal.domain.models.AdDraftDomain
import gearhub.feature.products.product_feature.internal.domain.models.CreateAdPayloadDomain
import gearhub.feature.products.product_feature.internal.domain.models.UpdateAdPayloadDomain
import gearhub.feature.products.product_service.api.AdsApi

class AdsRepositoryImpl(
    private val api: AdsApi,
    private val authSessionDbDriver: AuthSessionDbDriver,
) : AdsRepository {
    override suspend fun createAd(payload: CreateAdPayloadDomain): ApiResponse<AdDraftDomain> {
        val authHeader = authSessionDbDriver.getCredentials()
            ?.accessToken
            ?.takeIf { it.isNotBlank() }
            ?.let { "Bearer $it" }
        return when (val response = api.createAd(payload.toData(), authHeader)) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomain())
            is ApiResponse.HttpError -> response
            ApiResponse.NetworkError -> ApiResponse.NetworkError
            is ApiResponse.UnknownError -> response
        }
    }

    override suspend fun updateAd(id: String, payload: UpdateAdPayloadDomain): ApiResponse<AdDraftDomain> {
        val authHeader = authSessionDbDriver.getCredentials()
            ?.accessToken
            ?.takeIf { it.isNotBlank() }
            ?.let { "Bearer $it" }
        return when (val response = api.updateAd(id, payload.toData(), authHeader)) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomain())
            is ApiResponse.HttpError -> response
            ApiResponse.NetworkError -> ApiResponse.NetworkError
            is ApiResponse.UnknownError -> response
        }
    }
}
