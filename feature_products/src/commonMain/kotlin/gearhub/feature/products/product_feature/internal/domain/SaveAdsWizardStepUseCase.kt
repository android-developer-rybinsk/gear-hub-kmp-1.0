package gearhub.feature.products.product_feature.internal.domain

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.domain.models.AdsSavePayloadDomainModel
import gearhub.feature.products.product_feature.internal.domain.models.AdsSaveResultDomainModel
import kotlinx.serialization.json.JsonElement

class SaveAdsWizardStepUseCase(
    private val repository: AdsRepository,
) {
    suspend operator fun invoke(
        categoryId: Int,
        id: String?,
        attributes: Map<String, JsonElement>,
    ): ApiResponse<AdsSaveResultDomainModel> {
        return repository.save(
            AdsSavePayloadDomainModel(
                categoryId = if (id == null) categoryId else null,
                id = id,
                attributes = attributes,
            ),
        )
    }
}
