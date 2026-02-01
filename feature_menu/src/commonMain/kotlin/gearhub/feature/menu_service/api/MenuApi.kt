package gearhub.feature.menu_service.api

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.menu_feature.internal.data.model.MenuCategoryDto

/**
 * Контракт сетевых вызовов меню.
 */
interface MenuApi {
    /**
     * Получить список категорий.
     */
    suspend fun getCategories(): ApiResponse<List<MenuCategoryDto>>
}
