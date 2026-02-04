package gearhub.feature.menu_feature.internal.presentation.search

import gearhub.feature.menu_feature.internal.presentation.menu.MenuProduct

data class SearchResultsState(
    val query: String = "",
    val results: List<MenuProduct> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
