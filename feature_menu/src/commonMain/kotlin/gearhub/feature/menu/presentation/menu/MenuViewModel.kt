package gearhub.feature.menu.presentation.menu

import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import gearhub.feature.menu.navigation.DestinationMenu
import gearhub.feature.menu.navigation.FilterArgs
import gearhub.feature.menu.navigation.ProductDetailsArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.random.Random

class MenuViewModel(
    private val router: Router
) : BaseViewModel<MenuState, MenuAction>(MenuState()) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val categoriesSource = MutableStateFlow(seedCategories())
    private val productsSource = MutableStateFlow(seedProducts())

    private var currentPage = 0
    private val pageSize = 6

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
                loadInitial()
            }
            MenuAction.FilterClicked -> router.navigate(DestinationMenu.FilterScreen())
            is MenuAction.CategorySelected -> router.navigate(
                DestinationMenu.FilterScreen(FilterArgs(categoryId = action.categoryId))
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
        val query = currentState.searchQuery.trim()

        return productsSource.value.filter { product ->
            query.isBlank() || product.title.contains(query, ignoreCase = true)
        }
    }

    private fun seedCategories(): List<MenuCategory> = listOf(
        MenuCategory("boats", "Лодки"),
        MenuCategory("service", "Сервис"),
        MenuCategory("tackle", "Снасти"),
        MenuCategory("outfit", "Экипировка"),
        MenuCategory("accessories", "Аксессуары")
    )

    private fun seedProducts(): List<MenuProduct> {
        val prices = listOf(4500.0, 18990.0, 12999.0, 7500.0, 3990.0, 6200.0, 28450.0, 1190.0)
        val titles = listOf(
            "Надувная лодка",
            "Эхолот Garmin",
            "Набор для сервиса",
            "Спиннинг для трофея",
            "Костюм для рыбалки",
            "Набор воблеров",
            "Электромотор",
            "Шнур плетеный"
        )
        val categories = seedCategories()

        return List(40) { index ->
                val category = categories.random()
                MenuProduct(
                    id = "product-$index",
                    title = titles[index % titles.size] + " #${index + 1}",
                    price = prices[index % prices.size] + Random.nextInt(0, 5000),
                    imageUrl = null,
                    categoryId = category.id
                )
        }
    }
}
