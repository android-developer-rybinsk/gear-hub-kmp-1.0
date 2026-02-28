package gearhub.feature.menu_feature.internal.presentation.search

import com.gear.hub.network.model.ApiResponse
import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import gearhub.feature.menu_feature.api.presentation.models.MenuProductUI
import gearhub.feature.menu_feature.internal.presentation.filter.MenuFilterStore
import gearhub.feature.menu_feature.internal.presentation.menu.MenuDataProvider
import gearhub.feature.menu_feature.navigation.DestinationMenu
import gearhub.feature.menu_feature.navigation.ProductDetailsArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SearchResultsViewModel(
    private val router: Router,
    private val dataProvider: MenuDataProvider,
    initialQuery: String,
) : BaseViewModel<SearchResultsState, SearchResultsAction>(SearchResultsState(query = initialQuery)) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var products: List<MenuProductUI> = emptyList()

    init {
        initialize(initialQuery)
    }

    private fun initialize(initialQuery: String) {
        MenuFilterStore.update { it.copy(query = initialQuery) }
        loadProductsAndFilter(initialQuery)
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
                applyFilter(action.value)
            }

            is SearchResultsAction.ProductClicked -> router.navigate(
                DestinationMenu.DetailsScreen(ProductDetailsArgs(productId = action.productId))
            )

            SearchResultsAction.FilterClicked -> router.navigate(
                DestinationMenu.FilterScreen()
            )
        }
    }

    private fun loadProductsAndFilter(query: String) {
        scope.launch {
            setState { it.copy(isLoading = true, errorMessage = null) }
            when (val response = dataProvider.products(limit = 100, cursor = null)) {
                is ApiResponse.Success -> {
                    products = response.data.data
                    applyFilter(query)
                }
                is ApiResponse.HttpError -> setState {
                    it.copy(isLoading = false, errorMessage = response.message ?: "Ошибка сервера")
                }
                ApiResponse.NetworkError -> setState {
                    it.copy(isLoading = false, errorMessage = "Нет соединения с сервером")
                }
                is ApiResponse.UnknownError -> setState {
                    it.copy(isLoading = false, errorMessage = response.throwable?.message)
                }
            }
        }
    }

    private fun applyFilter(query: String) {
        scope.launch {
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
                    errorMessage = null,
                )
            }
        }
    }
}
