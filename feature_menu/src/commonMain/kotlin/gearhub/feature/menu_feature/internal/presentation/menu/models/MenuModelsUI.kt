package gearhub.feature.menu_feature.internal.presentation.menu.models

import gearhub.feature.menu_feature.internal.domain.models.MenuCategoryDomain

data class MenuCategoryUI(
    val id: String,
    val title: String,
)

data class MenuProductUI(
    val id: String,
    val title: String,
    val price: Double,
    val imageUrl: String? = null,
    val categoryId: String? = null,
)

internal fun MenuCategoryDomain.toUI(): MenuCategoryUI = MenuCategoryUI(
    id = id,
    title = title,
)
