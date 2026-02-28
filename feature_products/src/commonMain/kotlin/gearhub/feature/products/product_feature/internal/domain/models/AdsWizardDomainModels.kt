package gearhub.feature.products.product_feature.internal.domain.models

import kotlinx.serialization.json.JsonElement

data class AdsWizardPayloadDomainModel(
    val categoryId: Int,
    val id: String? = null,
    val fieldsValues: List<AdsWizardFieldInputDomainModel> = emptyList(),
)

data class AdsWizardFieldInputDomainModel(
    val key: String,
    val value: JsonElement,
)

data class AdsSavePayloadDomainModel(
    val categoryId: Int? = null,
    val id: String? = null,
    val attributes: Map<String, JsonElement> = emptyMap(),
)

data class AdsWizardResultDomainModel(
    val fields: List<AdsWizardFieldDomainModel> = emptyList(),
    val steps: List<AdsWizardStepDomainModel> = emptyList(),
    val currentStep: Int? = null,
)

data class AdsWizardFieldDomainModel(
    val key: String,
    val label: String,
    val required: Boolean,
    val requiresReload: Boolean,
    val stepSlug: String?,
    val widgetType: String,
    val value: JsonElement?,
    val validation: JsonElement?,
    val values: List<AdsWizardFieldValueDomainModel>,
)

data class AdsWizardFieldValueDomainModel(
    val label: String,
    val value: JsonElement,
)

data class AdsWizardStepDomainModel(
    val slug: String,
    val title: String,
    val sorting: Int,
    val children: List<String>,
)

data class AdsSaveResultDomainModel(
    val id: String,
    val attributes: Map<String, JsonElement>,
)
