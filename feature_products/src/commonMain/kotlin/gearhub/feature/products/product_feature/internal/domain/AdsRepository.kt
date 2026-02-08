package gearhub.feature.products.product_feature.internal.domain

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.domain.models.AdDraft
import gearhub.feature.products.product_feature.internal.domain.models.CreateAdPayload
import gearhub.feature.products.product_feature.internal.domain.models.UpdateAdPayload

interface AdsRepository {
    suspend fun createAd(payload: CreateAdPayload): ApiResponse<AdDraft>

    suspend fun updateAd(id: String, payload: UpdateAdPayload): ApiResponse<AdDraft>
}
