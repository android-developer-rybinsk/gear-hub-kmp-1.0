package gearhub.feature.products.product_feature.internal.domain

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.domain.models.*

interface AdsRepository {
    suspend fun getAds(limit: Int = 20, cursor: String? = null): ApiResponse<AdsPageDomain>

    suspend fun wizard(payload: AdsWizardPayloadDomainModel): ApiResponse<AdsWizardResultDomainModel>

    suspend fun save(payload: AdsSavePayloadDomainModel): ApiResponse<AdsSaveResultDomainModel>

    suspend fun createAd(payload: CreateAdPayloadDomain): ApiResponse<AdDraftDomain>

    suspend fun updateAd(id: String, payload: UpdateAdPayloadDomain): ApiResponse<AdDraftDomain>
}
