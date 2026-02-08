package gearhub.feature.menu_feature.internal.presentation.search

import gearhub.feature.menu_feature.internal.presentation.menu.models.MenuProductUI

data class SearchResultsState(
    val query: String = "",
    val results: List<MenuProductUI> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
