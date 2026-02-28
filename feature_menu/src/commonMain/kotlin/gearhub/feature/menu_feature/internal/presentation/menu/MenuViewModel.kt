package gearhub.feature.menu_feature.internal.presentation.menu

import com.gear.hub.network.model.ApiResponse
import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import gearhub.feature.menu_feature.api.MenuViewModelApi
import gearhub.feature.menu_feature.api.presentation.MenuAction
import gearhub.feature.menu_feature.api.presentation.MenuStateUI
import gearhub.feature.menu_feature.api.presentation.models.toUI
import gearhub.feature.menu_feature.internal.domain.MenuCategoriesUseCase
import gearhub.feature.menu_feature.navigation.DestinationMenu
import gearhub.feature.menu_feature.navigation.FilterArgs
import gearhub.feature.menu_feature.navigation.ProductDetailsArgs
import gearhub.feature.menu_feature.navigation.SearchArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class MenuViewModel(
    private val router: Router,
    private val categoriesUseCase: MenuCategoriesUseCase,
    private val dataProvider: MenuDataProvider,
) : BaseViewModel<MenuStateUI, MenuAction>(MenuStateUI()), MenuViewModelApi {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val categoriesSource = categoriesUseCase.categories

    private var nextCursor: String? = null
    private var hasNextPage: Boolean = false
    private var lastSearchNavigationQuery: String? = null

    init {
        initialize()
    }

    private fun initialize() {
        syncCategories()
        observeCategories()
        loadInitial()
    }

    private fun syncCategories() {
        scope.launch {
            when (val response = categoriesUseCase.refreshCategories()) {
                is ApiResponse.HttpError -> setCategoryError(response.message)
                ApiResponse.NetworkError -> setCategoryError("Нет соединения с сервером")
                is ApiResponse.UnknownError -> setCategoryError("Не удалось загрузить категории")
                is ApiResponse.Success -> Unit
            }
            categoriesUseCase.loadFromDb()
        }
    }

    private fun observeCategories() {
        scope.launch {
            categoriesSource.collect { categories ->
                setState { state ->
                    state.copy(categories = categories.map { it.toUI() })
                }
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
                    products = emptyList(),
                )
            }

            when (val response = dataProvider.products(limit = PAGE_LIMIT, cursor = null)) {
                is ApiResponse.Success -> {
                    nextCursor = response.data.nextCursor
                    hasNextPage = response.data.hasNextPage
                    setState {
                        it.copy(
                            products = response.data.data,
                            isLoading = false,
                            isPaginating = false,
                            endReached = !hasNextPage,
                            errorMessage = null,
                        )
                    }
                }
                is ApiResponse.HttpError -> setState {
                    it.copy(isLoading = false, isPaginating = false, errorMessage = response.message ?: "Ошибка сервера")
                }
                ApiResponse.NetworkError -> setState {
                    it.copy(isLoading = false, isPaginating = false, errorMessage = "Нет соединения с сервером")
                }
                is ApiResponse.UnknownError -> setState {
                    it.copy(
                        isLoading = false,
                        isPaginating = false,
                        errorMessage = response.throwable?.message ?: "Не удалось загрузить объявления",
                    )
                }
            }
        }
    }

    private fun loadNextPage() {
        if (currentState.isPaginating || currentState.isLoading || !hasNextPage || nextCursor == null) return

        scope.launch {
            setState { it.copy(isPaginating = true, errorMessage = null) }
            when (val response = dataProvider.products(limit = PAGE_LIMIT, cursor = nextCursor)) {
                is ApiResponse.Success -> {
                    nextCursor = response.data.nextCursor
                    hasNextPage = response.data.hasNextPage
                    setState {
                        it.copy(
                            products = it.products + response.data.data,
                            isPaginating = false,
                            endReached = !hasNextPage,
                            errorMessage = null,
                        )
                    }
                }
                is ApiResponse.HttpError -> setState {
                    it.copy(isPaginating = false, errorMessage = response.message ?: "Ошибка сервера")
                }
                ApiResponse.NetworkError -> setState {
                    it.copy(isPaginating = false, errorMessage = "Нет соединения с сервером")
                }
                is ApiResponse.UnknownError -> setState {
                    it.copy(
                        isPaginating = false,
                        errorMessage = response.throwable?.message ?: "Ошибка загрузки страницы",
                    )
                }
            }
        }
    }

    private fun setCategoryError(message: String?) {
        if (!message.isNullOrBlank()) {
            setState { it.copy(errorMessage = message) }
        }
    }

    private companion object {
        const val PAGE_LIMIT = 20
    }
}
