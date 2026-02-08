package gearhub.feature.products.product_feature.internal.presentation.create

import gearhub.feature.products.product_feature.internal.presentation.create.models.AdCategoryUI
import gearhub.feature.products.product_feature.internal.presentation.create.models.CreateAdStepUI

data class CreateAdStateUI(
    val step: CreateAdStepUI = CreateAdStepUI.Category,
    val categories: List<AdCategoryUI> = emptyList(),
    val selectedCategory: AdCategoryUI? = null,
    val title: String = "",
    val brand: String = "",
    val model: String = "",
    val vin: String = "",
    val location: String = "",
    val condition: String = "",
    val description: String = "",
    val price: String = "",
    val adId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
