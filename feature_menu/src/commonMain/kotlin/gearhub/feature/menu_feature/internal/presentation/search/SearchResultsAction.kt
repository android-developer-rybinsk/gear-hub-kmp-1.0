package gearhub.feature.menu_feature.internal.presentation.search

sealed interface SearchResultsAction {
    data object Back : SearchResultsAction
    data class QueryChanged(val value: String) : SearchResultsAction
    data class ProductClicked(val productId: String) : SearchResultsAction
    data object FilterClicked : SearchResultsAction
}
