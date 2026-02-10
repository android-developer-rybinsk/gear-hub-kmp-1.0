package gearhub.feature.products.product_feature.internal.presentation.create.models

import gearhub.feature.menu_feature.api.models.MenuCategoryInfoModel

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

internal fun MenuCategoryInfoModel.toUI(): AdCategoryUI = AdCategoryUI(
    id = id,
    title = title,
    slug = slug,
)
