package gearhub.feature.menu_feature.api

import gearhub.feature.menu_feature.api.models.MenuCategoryInfo

/**
 * Публичный доступ к списку категорий, сохранённых в меню.
 */
interface MenuCategoryProvider {
    suspend fun getCategories(): List<MenuCategoryInfo>
}
