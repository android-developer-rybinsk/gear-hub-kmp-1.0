package gearhub.feature.products.product_feature.internal.data.models

import gearhub.feature.products.product_feature.internal.domain.models.AdsSavePayloadDomainModel
import gearhub.feature.products.product_feature.internal.domain.models.AdsSaveResultDomainModel
import gearhub.feature.products.product_feature.internal.domain.models.AdsWizardFieldDomainModel
import gearhub.feature.products.product_feature.internal.domain.models.AdsWizardFieldValueDomainModel
import gearhub.feature.products.product_feature.internal.domain.models.AdsWizardPayloadDomainModel
import gearhub.feature.products.product_feature.internal.domain.models.AdsWizardResultDomainModel
import gearhub.feature.products.product_feature.internal.domain.models.AdsWizardStepDomainModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AdsWizardRequestDataModel(
    @SerialName("categoryId")
    val categoryId: Int? = null,
    @SerialName("fieldsValues")
    val fieldsValues: List<AdsWizardFieldValueDataModel> = emptyList(),
)

@Serializable
data class AdsWizardFieldValueDataModel(
    @SerialName("key")
    val key: String,
    @SerialName("value")
    val value: JsonElement,
)

@Serializable
data class AdsSaveRequestDataModel(
    @SerialName("categoryId")
    val categoryId: Int? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("attributes")
    val attributes: Map<String, JsonElement> = emptyMap(),
)

@Serializable
data class AdsWizardResponseDataModel(
    @SerialName("fields")
    val fields: List<AdsWizardFieldDataModel> = emptyList(),
    @SerialName("steps")
    val steps: List<AdsWizardStepDataModel> = emptyList(),
    @SerialName("currentStep")
    val currentStep: Int? = null,
)

@Serializable
data class AdsWizardFieldDataModel(
    @SerialName("key")
    val key: String,
    @SerialName("label")
    val label: String? = null,
    @SerialName("required")
    val required: Boolean = false,
    @SerialName("requiresReload")
    val requiresReload: Boolean = false,
    @SerialName("stepSlug")
    val stepSlug: String? = null,
    @SerialName("widget")
    val widget: AdsWizardWidgetDataModel? = null,
    @SerialName("value")
    val value: JsonElement? = null,
    @SerialName("validation")
    val validation: JsonElement? = null,
    @SerialName("values")
    val values: List<AdsWizardOptionDataModel> = emptyList(),
)

@Serializable
data class AdsWizardWidgetDataModel(
    @SerialName("type")
    val type: String,
)

@Serializable
data class AdsWizardOptionDataModel(
    @SerialName("label")
    val label: String,
    @SerialName("value")
    val value: JsonElement,
)

@Serializable
data class AdsWizardStepDataModel(
    @SerialName("slug")
    val slug: String,
    @SerialName("title")
    val title: String,
    @SerialName("sorting")
    val sorting: Int,
    @SerialName("children")
    val children: List<String> = emptyList(),
)

@Serializable
data class AdsSaveResponseDataModel(
    @SerialName("id")
    val id: String,
    @SerialName("attributes")
    val attributes: Map<String, JsonElement> = emptyMap(),
)

internal fun AdsWizardPayloadDomainModel.toData(): AdsWizardRequestDataModel = AdsWizardRequestDataModel(
    categoryId = categoryId,
    fieldsValues = fieldsValues.map { AdsWizardFieldValueDataModel(it.key, it.value) },
)

internal fun AdsSavePayloadDomainModel.toData(): AdsSaveRequestDataModel = AdsSaveRequestDataModel(
    categoryId = categoryId,
    id = id,
    attributes = attributes,
)

internal fun AdsWizardResponseDataModel.toDomain(): AdsWizardResultDomainModel = AdsWizardResultDomainModel(
    fields = fields.map { field ->
        AdsWizardFieldDomainModel(
            key = field.key,
            label = field.label.orEmpty(),
            required = field.required,
            requiresReload = field.requiresReload,
            stepSlug = field.stepSlug,
            widgetType = field.widget?.type.orEmpty(),
            value = field.value,
            validation = field.validation,
            values = field.values.map { AdsWizardFieldValueDomainModel(it.label, it.value) },
        )
    },
    steps = steps.map { step ->
        AdsWizardStepDomainModel(
            slug = step.slug,
            title = step.title,
            sorting = step.sorting,
            children = step.children,
        )
    },
    currentStep = currentStep,
)

internal fun AdsSaveResponseDataModel.toDomain(): AdsSaveResultDomainModel = AdsSaveResultDomainModel(
    id = id,
    attributes = attributes,
)
