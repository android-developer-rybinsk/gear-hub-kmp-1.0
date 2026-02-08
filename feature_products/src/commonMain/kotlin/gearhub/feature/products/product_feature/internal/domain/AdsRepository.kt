package gearhub.feature.products.product_feature.internal.domain

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.domain.models.AdDraftDomain
import gearhub.feature.products.product_feature.internal.domain.models.CreateAdPayloadDomain
import gearhub.feature.products.product_feature.internal.domain.models.UpdateAdPayloadDomain

interface AdsRepository {
    suspend fun createAd(payload: CreateAdPayloadDomain): ApiResponse<AdDraftDomain>

    suspend fun updateAd(id: String, payload: UpdateAdPayloadDomain): ApiResponse<AdDraftDomain>
}
