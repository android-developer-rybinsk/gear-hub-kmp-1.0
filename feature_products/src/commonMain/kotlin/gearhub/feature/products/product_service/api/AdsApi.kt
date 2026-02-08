package gearhub.feature.products.product_service.api

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.data.models.CreateAdRequestDto
import gearhub.feature.products.product_feature.internal.data.models.CreateAdResponseDto
import gearhub.feature.products.product_feature.internal.data.models.UpdateAdRequestDto

/**
 * Контракт сетевых вызовов для объявлений.
 */
interface AdsApi {
    suspend fun createAd(request: CreateAdRequestDto): ApiResponse<CreateAdResponseDto>

    suspend fun updateAd(id: String, request: UpdateAdRequestDto): ApiResponse<CreateAdResponseDto>
}
