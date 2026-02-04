package gearhub.feature.products.service.api

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.internal.data.model.CreateAdRequestDto
import gearhub.feature.products.internal.data.model.CreateAdResponseDto
import gearhub.feature.products.internal.data.model.UpdateAdRequestDto

/**
 * Контракт сетевых вызовов для объявлений.
 */
interface AdsApi {
    suspend fun createAd(request: CreateAdRequestDto): ApiResponse<CreateAdResponseDto>

    suspend fun updateAd(id: String, request: UpdateAdRequestDto): ApiResponse<CreateAdResponseDto>
}
