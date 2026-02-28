package gearhub.feature.products.product_feature.internal.presentation.create.models

import gearhub.feature.products.product_feature.internal.domain.models.AdsWizardFieldDomainModel
import gearhub.feature.products.product_feature.internal.domain.models.AdsWizardFieldValueDomainModel
import gearhub.feature.products.product_feature.internal.domain.models.AdsWizardResultDomainModel
import gearhub.feature.products.product_feature.internal.domain.models.AdsWizardStepDomainModel
import kotlinx.serialization.json.JsonElement

data class AdsWizardResultUiModel(
    val fields: List<AdsWizardFieldUiModel> = emptyList(),
    val steps: List<AdsWizardStepUiModel> = emptyList(),
)

data class AdsWizardFieldUiModel(
    val key: String,
    val label: String,
    val required: Boolean,
    val requiresReload: Boolean,
    val stepSlug: String?,
    val widgetType: String,
    val value: JsonElement?,
    val values: List<AdsWizardFieldValueUiModel>,
)

data class AdsWizardFieldValueUiModel(
    val label: String,
    val value: JsonElement,
)

data class AdsWizardStepUiModel(
    val slug: String,
    val title: String,
    val sorting: Int,
    val children: List<String>,
)

internal fun AdsWizardResultDomainModel.toUi(): AdsWizardResultUiModel = AdsWizardResultUiModel(
    fields = fields.map { it.toUi() },
    steps = steps.sortedBy { it.sorting }.map { it.toUi() },
)

internal fun AdsWizardFieldDomainModel.toUi(): AdsWizardFieldUiModel = AdsWizardFieldUiModel(
    key = key,
    label = label,
    required = required,
    requiresReload = requiresReload,
    stepSlug = stepSlug,
    widgetType = widgetType,
    value = value,
    values = values.map { it.toUi() },
)

internal fun AdsWizardFieldValueDomainModel.toUi(): AdsWizardFieldValueUiModel = AdsWizardFieldValueUiModel(
    label = label,
    value = value,
)

internal fun AdsWizardStepDomainModel.toUi(): AdsWizardStepUiModel = AdsWizardStepUiModel(
    slug = slug,
    title = title,
    sorting = sorting,
    children = children,
)
