package gearhub.feature.products.product_service.api

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.data.models.CreateAdRequestDTO
import gearhub.feature.products.product_feature.internal.data.models.CreateAdResponseDTO
import gearhub.feature.products.product_feature.internal.data.models.UpdateAdRequestDTO

/**
 * Контракт сетевых вызовов для объявлений.
 */
interface AdsApi {
    suspend fun createAd(
        request: CreateAdRequestDTO,
        authHeader: String? = null,
    ): ApiResponse<CreateAdResponseDTO>

    suspend fun updateAd(
        id: String,
        request: UpdateAdRequestDTO,
        authHeader: String? = null,
    ): ApiResponse<CreateAdResponseDTO>
}
