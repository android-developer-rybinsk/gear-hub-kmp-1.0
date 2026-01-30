package gearhub.feature.menu.presentation.menu

import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import gearhub.feature.menu.data.MenuDataProvider
import gearhub.feature.menu.navigation.DestinationMenu
import gearhub.feature.menu.navigation.FilterArgs
import gearhub.feature.menu.navigation.ProductDetailsArgs
import gearhub.feature.menu.navigation.SearchArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MenuViewModel(
    private val router: Router
) : BaseViewModel<MenuState, MenuAction>(MenuState()) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val categoriesSource = MutableStateFlow(MenuDataProvider.categories())
    private val productsSource = MutableStateFlow(MenuDataProvider.products())

    private var currentPage = 0
    private val pageSize = 6
    private var lastSearchNavigationQuery: String? = null

    init {
        scope.launch {
            combine(
                categoriesSource,
                productsSource
            ) { categories, _ ->
                categories to Unit
            }.collect { (categories, _) ->
                setState { state ->
                    state.copy(categories = categories)
                }
            }
        }

        scope.launch {
            productsSource.collectLatest {
                loadInitial()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

    override fun onAction(action: MenuAction) {
        when (action) {
            MenuAction.Back -> router.back()
            is MenuAction.SearchChanged -> {
                setState { it.copy(searchQuery = action.query) }
                val trimmed = action.query.trim()
                if (trimmed.isEmpty()) {
                    lastSearchNavigationQuery = null
                }
            }
            MenuAction.SearchSubmitted -> {
                val trimmed = currentState.searchQuery.trim()
                if (trimmed.isEmpty()) {
                    lastSearchNavigationQuery = null
                    return
                }

                if (trimmed != lastSearchNavigationQuery) {
                    router.navigate(DestinationMenu.SearchResultsScreen(SearchArgs(trimmed)))
                    setState { it.copy(searchQuery = "") }
                    lastSearchNavigationQuery = null
                }
            }
            MenuAction.FilterClicked -> router.navigate(DestinationMenu.FilterScreen())
            is MenuAction.CategorySelected -> router.navigate(
                DestinationMenu.FilterScreen(FilterArgs(categoryId = action.categoryId))
            )
            MenuAction.FilterApplied -> router.navigate(
                DestinationMenu.SearchResultsScreen(SearchArgs(currentState.searchQuery.trim()))
            )
            is MenuAction.ProductClicked -> router.navigate(
                DestinationMenu.DetailsScreen(ProductDetailsArgs(productId = action.productId))
            )
            MenuAction.LoadNextPage -> loadNextPage()
            MenuAction.Retry -> loadInitial()
        }
    }

    private fun loadInitial() {
        scope.launch {
            setState {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    endReached = false,
                    products = emptyList()
                )
            }

            try {
                delay(250)
                currentPage = 0
                val filtered = filterProducts()
                val page = filtered.take(pageSize)
                setState {
                    it.copy(
                        products = page,
                        isLoading = false,
                        endReached = page.size >= filtered.size
                    )
                }
            } catch (e: Exception) {
                setState {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Не удалось загрузить объявления"
                    )
                }
            }
        }
    }

    private fun loadNextPage() {
        val state = currentState
        if (state.isPaginating || state.isLoading || state.endReached) return

        scope.launch {
            setState { it.copy(isPaginating = true, errorMessage = null) }
            try {
                delay(300)
                val filtered = filterProducts()
                val nextPage = currentPage + 1
                val fromIndex = nextPage * pageSize
                if (fromIndex >= filtered.size) {
                    setState { it.copy(isPaginating = false, endReached = true) }
                    return@launch
                }
                val toIndex = minOf(fromIndex + pageSize, filtered.size)
                val nextItems = filtered.subList(fromIndex, toIndex)
                setState {
                    it.copy(
                        products = it.products + nextItems,
                        isPaginating = false,
                        endReached = toIndex >= filtered.size
                    )
                }
                currentPage = nextPage
            } catch (e: Exception) {
                setState {
                    it.copy(
                        isPaginating = false,
                        errorMessage = e.message ?: "Ошибка загрузки страницы"
                    )
                }
            }
        }
    }

    private fun filterProducts(): List<MenuProduct> {
        return productsSource.value
    }
}
