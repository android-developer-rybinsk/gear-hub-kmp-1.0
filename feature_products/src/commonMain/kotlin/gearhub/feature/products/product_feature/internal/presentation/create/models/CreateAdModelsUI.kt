package gearhub.feature.products.product_feature.internal.presentation.create.models

import gearhub.feature.products.product_feature.api.ProductCategoryInfoModel

data class AdCategoryUI(
    val id: String,
    val title: String,
    val slug: String,
)

enum class CreateAdStepUI {
    Category,
    Title,
    Vin,
    Details,
    Description,
    Photos,
    Price,
}

internal fun ProductCategoryInfoModel.toUI(): AdCategoryUI = AdCategoryUI(
    id = id,
    title = title,
    slug = slug,
)
