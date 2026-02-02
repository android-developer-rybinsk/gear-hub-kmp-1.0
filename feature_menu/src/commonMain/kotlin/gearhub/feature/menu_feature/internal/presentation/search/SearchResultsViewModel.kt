package gearhub.feature.menu_feature.internal.presentation.search

import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import gearhub.feature.menu_feature.internal.data.MenuDataProvider
import gearhub.feature.menu_feature.navigation.DestinationMenu
import gearhub.feature.menu_feature.navigation.ProductDetailsArgs
import gearhub.feature.menu_feature.internal.presentation.filter.MenuFilterStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchResultsViewModel(
    private val router: Router,
    initialQuery: String
) : BaseViewModel<SearchResultsState, SearchResultsAction>(SearchResultsState(query = initialQuery)) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val products = MenuDataProvider.products()

    init {
        initialize(initialQuery)
    }

    private fun initialize(initialQuery: String) {
        MenuFilterStore.update { it.copy(query = initialQuery) }
        loadResults(initialQuery)
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

    override fun onAction(action: SearchResultsAction) {
        when (action) {
            SearchResultsAction.Back -> {
                MenuFilterStore.reset()
                router.back()
            }
            is SearchResultsAction.QueryChanged -> {
                setState { it.copy(query = action.value) }
                MenuFilterStore.update { it.copy(query = action.value) }
                loadResults(action.value)
            }

            is SearchResultsAction.ProductClicked -> router.navigate(
                DestinationMenu.DetailsScreen(ProductDetailsArgs(productId = action.productId))
            )

            SearchResultsAction.FilterClicked -> router.navigate(
                DestinationMenu.FilterScreen()
            )
        }
    }

    private fun loadResults(query: String) {
        scope.launch {
            setState { it.copy(isLoading = true, errorMessage = null) }
            try {
                delay(150)
                val filterState = MenuFilterStore.state().value
                val minPrice = filterState.priceFrom.toDoubleOrNull()
                val maxPrice = filterState.priceTo.toDoubleOrNull()
                val filtered = products.filter { product ->
                    val matchesQuery = query.isBlank() || product.title.contains(query, ignoreCase = true)
                    val matchesCategory = filterState.selectedCategoryId == null || product.categoryId == filterState.selectedCategoryId
                    val matchesPrice = (minPrice == null || product.price >= minPrice) && (maxPrice == null || product.price <= maxPrice)
                    matchesQuery && matchesCategory && matchesPrice
                }
                setState {
                    it.copy(
                        results = filtered,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                setState { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
