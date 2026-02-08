package gearhub.feature.menu_feature.internal.data

import com.gear.hub.network.model.ApiResponse
import com.gear.hub.network.model.map
import gearhub.feature.menu_feature.api.db.MenuCategoryDbDriver
import gearhub.feature.menu_feature.api.models.MenuCategoryModel
import gearhub.feature.menu_feature.internal.data.models.MenuCategoryDTO
import gearhub.feature.menu_feature.internal.data.mappers.toDomain
import gearhub.feature.menu_feature.internal.domain.models.MenuCategoryDomain
import gearhub.feature.menu_service.api.MenuApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

internal interface MenuCategoryRepository {
    val categories: StateFlow<List<MenuCategoryDomain>>

    suspend fun loadFromDb()

    suspend fun refreshCategories(): ApiResponse<Unit>
}

internal class MenuCategoryRepositoryImpl(
    private val api: MenuApi,
    private val dbDriver: MenuCategoryDbDriver,
) : MenuCategoryRepository {
    private val categoriesState = MutableStateFlow<List<MenuCategoryDomain>>(emptyList())

    override val categories: StateFlow<List<MenuCategoryDomain>> = categoriesState.asStateFlow()

    override suspend fun loadFromDb() {
        val records = withContext(Dispatchers.IO) { dbDriver.getCategories() }
        categoriesState.value = records
            .filter { it.parentId == null }
            .sortedBy { it.position }
            .map { it.toDomain() }
    }

    override suspend fun refreshCategories(): ApiResponse<Unit> {
        val response = api.getCategories()
        if (response is ApiResponse.Success) {
            val records = response.data.toRecords()
            withContext(Dispatchers.IO) { dbDriver.setCategories(records) }
        }
        return response.map { Unit }
    }

    private fun List<MenuCategoryDTO>.toRecords(): List<MenuCategoryModel> {
        val result = mutableListOf<MenuCategoryModel>()
        val positionCounter = intArrayOf(0)

        fun append(category: MenuCategoryDTO) {
            val position = positionCounter[0]++
            result.add(
                MenuCategoryModel(
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
