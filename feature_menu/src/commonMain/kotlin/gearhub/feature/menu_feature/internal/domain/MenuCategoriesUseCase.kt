package gearhub.feature.menu_feature.internal.domain

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.menu_feature.internal.domain.models.MenuCategoryDomain
import kotlinx.coroutines.flow.StateFlow

internal class MenuCategoriesUseCase(
    private val repository: MenuCategoryRepository,
) {
    val categories: StateFlow<List<MenuCategoryDomain>> = repository.categories

    suspend fun loadFromDb() = repository.loadFromDb()

    suspend fun refreshCategories(): ApiResponse<Unit> = repository.refreshCategories()
}
