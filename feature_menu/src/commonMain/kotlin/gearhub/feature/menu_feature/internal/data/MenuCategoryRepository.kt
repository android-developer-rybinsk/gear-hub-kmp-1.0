package gearhub.feature.menu_feature.internal.data

import com.gear.hub.network.model.ApiResponse
import gearhub.feature.menu_feature.api.db.MenuCategoryDbDriver
import gearhub.feature.menu_feature.api.model.MenuCategoryRecord
import gearhub.feature.menu_feature.internal.data.model.MenuCategoryDto
import gearhub.feature.menu.presentation.menu.MenuCategory
import gearhub.feature.menu_service.api.MenuApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

internal class MenuCategoryRepository(
    private val api: MenuApi,
    private val dbDriver: MenuCategoryDbDriver,
) {
    private val categoriesState = MutableStateFlow<List<MenuCategory>>(emptyList())

    val categories: StateFlow<List<MenuCategory>> = categoriesState.asStateFlow()

    suspend fun loadFromDb() {
        val records = withContext(Dispatchers.IO) { dbDriver.getCategories() }
        categoriesState.value = records
            .filter { it.parentId == null }
            .sortedBy { it.position }
            .map { MenuCategory(id = it.id, title = it.name) }
    }

    suspend fun refreshCategories(): ApiResponse<Unit> {
        val response = api.getCategories()
        if (response is ApiResponse.Success) {
            val records = response.data.toRecords()
            withContext(Dispatchers.IO) { dbDriver.setCategories(records) }
        }
        return response.map { Unit }
    }

    private fun List<MenuCategoryDto>.toRecords(): List<MenuCategoryRecord> {
        val result = mutableListOf<MenuCategoryRecord>()
        val positionCounter = intArrayOf(0)

        fun append(category: MenuCategoryDto) {
            val position = positionCounter[0]++
            result.add(
                MenuCategoryRecord(
                    id = category.id.toString(),
                    slug = category.slug,
                    name = category.name,
                    parentId = category.parentId?.toString(),
                    position = position,
                ),
            )
            category.children.forEach { child -> append(child) }
        }

        forEach { append(it) }
        return result
    }
}
