package gearhub.feature.menu.presentation.search

import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import gearhub.feature.menu.data.MenuDataProvider
import gearhub.feature.menu.navigation.DestinationMenu
import gearhub.feature.menu.navigation.ProductDetailsArgs
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
        loadResults(initialQuery)
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

    override fun onAction(action: SearchResultsAction) {
        when (action) {
            SearchResultsAction.Back -> router.back()
            is SearchResultsAction.QueryChanged -> {
                setState { it.copy(query = action.value) }
                loadResults(action.value)
            }

            is SearchResultsAction.ProductClicked -> router.navigate(
                DestinationMenu.DetailsScreen(ProductDetailsArgs(productId = action.productId))
            )
        }
    }

    private fun loadResults(query: String) {
        scope.launch {
            setState { it.copy(isLoading = true, errorMessage = null) }
            try {
                delay(150)
                val filtered = products.filter { product ->
                    query.isBlank() || product.title.contains(query, ignoreCase = true)
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
