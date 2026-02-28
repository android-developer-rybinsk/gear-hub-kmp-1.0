package gearhub.feature.products.product_feature.internal.presentation.create

import gearhub.feature.products.product_feature.internal.presentation.create.models.AdCategoryUI
import gearhub.feature.products.product_feature.internal.presentation.create.models.AdsWizardResultUiModel
import kotlinx.serialization.json.JsonElement

data class CreateAdState(
    val categories: List<AdCategoryUI> = emptyList(),
    val selectedCategory: AdCategoryUI? = null,
    val adId: String? = null,
    val wizardResult: AdsWizardResultUiModel = AdsWizardResultUiModel(),
    val currentWizardStepIndex: Int = -1,
    val fieldValues: Map<String, JsonElement> = emptyMap(),
    val fieldInputValues: Map<String, String> = emptyMap(),
    val invalidFieldKeys: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
