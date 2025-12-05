package gearhub.feature.menu.presentation.menu

import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
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
    private val adsSource = MutableStateFlow(seedAds())

    private var currentPage = 0
    private val pageSize = 6

    init {
        scope.launch {
            combine(
                categoriesSource,
                adsSource
            ) { categories, _ ->
                categories to Unit
            }.collect { (categories, _) ->
                setState { state ->
                    state.copy(categories = categories)
                }
            }
        }

        scope.launch {
            adsSource.collectLatest {
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
            MenuAction.FilterClicked -> setState { it.copy(isFilterDialogOpen = !it.isFilterDialogOpen) }
            is MenuAction.CategorySelected -> {
                setState { it.copy(selectedCategoryId = action.categoryId) }
                loadInitial()
            }
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
                    ads = emptyList()
                )
            }

            try {
                delay(250)
                currentPage = 0
                val filtered = filterAds()
                val page = filtered.take(pageSize)
                setState {
                    it.copy(
                        ads = page,
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
                val filtered = filterAds()
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
                        ads = it.ads + nextItems,
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

    private fun filterAds(): List<MenuAd> {
        val query = currentState.searchQuery.trim()
        val categoryId = currentState.selectedCategoryId

        return adsSource.value.filter { ad ->
            (categoryId == null || ad.categoryId == categoryId) &&
                (query.isBlank() || ad.title.contains(query, ignoreCase = true))
        }
    }

    private fun seedCategories(): List<MenuCategory> = listOf(
        MenuCategory("all", "Все"),
        MenuCategory("boats", "Лодки"),
        MenuCategory("service", "Сервис"),
        MenuCategory("tackle", "Снасти"),
        MenuCategory("outfit", "Экипировка"),
        MenuCategory("accessories", "Аксессуары")
    )

    private fun seedAds(): List<MenuAd> {
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
            MenuAd(
                id = "ad-$index",
                title = titles[index % titles.size] + " #${index + 1}",
                price = prices[index % prices.size] + Random.nextInt(0, 5000),
                imageUrl = null,
                categoryId = if (category.id == "all") null else category.id
            )
        }
    }
}
