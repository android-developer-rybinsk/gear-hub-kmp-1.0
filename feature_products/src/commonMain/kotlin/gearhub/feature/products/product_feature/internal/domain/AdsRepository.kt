package gearhub.feature.products.product_feature.internal.domain

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.domain.models.AdsSavePayloadDomainModel
import gearhub.feature.products.product_feature.internal.domain.models.AdsSaveResultDomainModel
import gearhub.feature.products.product_feature.internal.domain.models.AdsWizardPayloadDomainModel
import gearhub.feature.products.product_feature.internal.domain.models.AdsWizardResultDomainModel

interface AdsRepository {
    suspend fun wizard(payload: AdsWizardPayloadDomainModel): ApiResponse<AdsWizardResultDomainModel>

    suspend fun save(payload: AdsSavePayloadDomainModel): ApiResponse<AdsSaveResultDomainModel>
}
