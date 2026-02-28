package gearhub.feature.products.product_service.api

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.data.models.AdsSaveRequestDataModel
import gearhub.feature.products.product_feature.internal.data.models.AdsSaveResponseDataModel
import gearhub.feature.products.product_feature.internal.data.models.AdsWizardRequestDataModel
import gearhub.feature.products.product_feature.internal.data.models.AdsWizardResponseDataModel

/**
 * Контракт сетевых вызовов для объявлений.
 */
interface AdsApi {
    suspend fun wizard(request: AdsWizardRequestDataModel): ApiResponse<AdsWizardResponseDataModel>

    suspend fun save(request: AdsSaveRequestDataModel): ApiResponse<AdsSaveResponseDataModel>
}
