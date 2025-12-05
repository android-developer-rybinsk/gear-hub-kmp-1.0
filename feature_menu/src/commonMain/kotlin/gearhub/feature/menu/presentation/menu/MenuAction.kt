package gearhub.feature.menu.presentation.menu

sealed class MenuAction {
    data object Back : MenuAction()
    data class SearchChanged(val query: String) : MenuAction()
    data object FilterClicked : MenuAction()
    data class CategorySelected(val categoryId: String?) : MenuAction()
    data object LoadNextPage : MenuAction()
    data object Retry : MenuAction()
}
