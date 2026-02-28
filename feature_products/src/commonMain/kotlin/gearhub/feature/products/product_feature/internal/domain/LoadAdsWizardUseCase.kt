package gearhub.feature.products.product_feature.internal.domain

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.products.product_feature.internal.domain.models.AdsWizardFieldInputDomainModel
import gearhub.feature.products.product_feature.internal.domain.models.AdsWizardPayloadDomainModel
import gearhub.feature.products.product_feature.internal.domain.models.AdsWizardResultDomainModel

class LoadAdsWizardUseCase(
    private val repository: AdsRepository,
) {
    suspend operator fun invoke(
        categoryId: Int,
        id: String? = null,
        fieldsValues: List<AdsWizardFieldInputDomainModel> = emptyList(),
    ): ApiResponse<AdsWizardResultDomainModel> {
        return repository.wizard(
            AdsWizardPayloadDomainModel(
                categoryId = categoryId,
                id = id,
                fieldsValues = fieldsValues,
            ),
        )
    }
}
