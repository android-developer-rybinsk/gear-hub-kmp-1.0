package gearhub.feature.menu_feature.internal.domain

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.menu_feature.internal.domain.models.MenuCategoryDomain
import kotlinx.coroutines.flow.StateFlow

internal interface MenuCategoryRepository {
    val categories: StateFlow<List<MenuCategoryDomain>>

    suspend fun loadFromDb()

    suspend fun refreshCategories(): ApiResponse<Unit>
}
