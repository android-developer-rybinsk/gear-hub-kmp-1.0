package gearhub.feature.menu_feature.api

/**
 * Публичный доступ к списку категорий, сохранённых в меню.
 */
interface MenuCategoryProvider {
    suspend fun getCategories(): List<MenuCategoryInfo>
}

data class MenuCategoryInfo(
    val id: String,
    val title: String,
    val slug: String,
)
