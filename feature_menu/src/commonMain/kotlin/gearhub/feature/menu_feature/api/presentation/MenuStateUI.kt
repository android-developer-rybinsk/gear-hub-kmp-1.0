package gearhub.feature.menu_feature.api.presentation

import gearhub.feature.menu_feature.api.presentation.models.MenuCategoryUI
import gearhub.feature.menu_feature.api.presentation.models.MenuProductUI

data class MenuStateUI(
    val searchQuery: String = "",
    val categories: List<MenuCategoryUI> = emptyList(),
    val products: List<MenuProductUI> = emptyList(),
    val isLoading: Boolean = true,
    val isPaginating: Boolean = false,
    val endReached: Boolean = false,
    val errorMessage: String? = null
)
