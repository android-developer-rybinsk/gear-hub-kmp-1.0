package gearhub.feature.products.product_feature.internal.data

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.data.models.toData
import gearhub.feature.products.product_feature.internal.data.models.toDomain
import gearhub.feature.products.product_feature.internal.domain.AdsRepository
import gearhub.feature.products.product_feature.internal.domain.models.*
import gearhub.feature.products.product_service.api.AdsApi

class AdsRepositoryImpl(
    private val api: AdsApi,
) : AdsRepository {

    override suspend fun getAds(limit: Int, cursor: String?): ApiResponse<AdsPageDomain> {
        return when (val response = api.getAds(limit = limit, cursor = cursor)) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomain())
            is ApiResponse.HttpError -> response
            ApiResponse.NetworkError -> ApiResponse.NetworkError
            is ApiResponse.UnknownError -> response
        }
    }

    override suspend fun wizard(payload: AdsWizardPayloadDomainModel): ApiResponse<AdsWizardResultDomainModel> {
        return when (val response = api.wizard(payload.toData())) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomain())
            is ApiResponse.HttpError -> response
            ApiResponse.NetworkError -> ApiResponse.NetworkError
            is ApiResponse.UnknownError -> response
        }
    }

    override suspend fun save(payload: AdsSavePayloadDomainModel): ApiResponse<AdsSaveResultDomainModel> {
        return when (val response = api.save(payload.toData())) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomain())
            is ApiResponse.HttpError -> response
            ApiResponse.NetworkError -> ApiResponse.NetworkError
            is ApiResponse.UnknownError -> response
        }
    }

    override suspend fun createAd(payload: CreateAdPayloadDomain): ApiResponse<AdDraftDomain> {
        return when (val response = api.createAd(payload.toData())) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomain())
            is ApiResponse.HttpError -> response
            ApiResponse.NetworkError -> ApiResponse.NetworkError
            is ApiResponse.UnknownError -> response
        }
    }

    override suspend fun updateAd(id: String, payload: UpdateAdPayloadDomain): ApiResponse<AdDraftDomain> {
        return when (val response = api.updateAd(id, payload.toData())) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomain())
            is ApiResponse.HttpError -> response
            ApiResponse.NetworkError -> ApiResponse.NetworkError
            is ApiResponse.UnknownError -> response
        }
    }
}
