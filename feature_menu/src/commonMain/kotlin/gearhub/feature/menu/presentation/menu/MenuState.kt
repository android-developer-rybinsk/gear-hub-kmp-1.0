package gearhub.feature.menu.presentation.menu

data class MenuCategory(
    val id: String,
    val title: String
)

data class MenuAd(
    val id: String,
    val title: String,
    val price: Double,
    val imageUrl: String? = null,
    val categoryId: String? = null
)

data class MenuState(
    val searchQuery: String = "",
    val categories: List<MenuCategory> = emptyList(),
    val ads: List<MenuAd> = emptyList(),
    val isLoading: Boolean = true,
    val isPaginating: Boolean = false,
    val endReached: Boolean = false,
    val errorMessage: String? = null
)
