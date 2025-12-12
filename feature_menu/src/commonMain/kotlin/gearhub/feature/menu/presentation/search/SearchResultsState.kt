package gearhub.feature.menu.presentation.search

import gearhub.feature.menu.presentation.menu.MenuProduct

data class SearchResultsState(
    val query: String = "",
    val results: List<MenuProduct> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
