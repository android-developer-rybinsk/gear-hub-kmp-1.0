package gearhub.feature.products.product_feature.internal.presentation.create

import kotlinx.serialization.json.JsonElement

sealed class CreateAdAction {
    data object LoadCategories : CreateAdAction()
    data class SelectCategory(val categoryId: String) : CreateAdAction()
    data class UpdateFieldInput(val key: String, val value: String) : CreateAdAction()
    data class SelectFieldValue(val key: String, val label: String, val value: JsonElement) : CreateAdAction()
    data object NextStep : CreateAdAction()
    data object Back : CreateAdAction()
}
