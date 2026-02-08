package gearhub.feature.menu_feature.internal.data

import gearhub.feature.menu_feature.api.MenuCategoryProvider
import gearhub.feature.menu_feature.api.db.MenuCategoryDbDriver
import gearhub.feature.menu_feature.api.models.MenuCategoryInfo
import gearhub.feature.menu_feature.internal.data.mappers.toDomain
import gearhub.feature.menu_feature.internal.data.mappers.toUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class MenuCategoryProviderImpl(
    private val dbDriver: MenuCategoryDbDriver,
) : MenuCategoryProvider {
    override suspend fun getCategories(): List<MenuCategoryInfo> = withContext(Dispatchers.IO) {
        dbDriver.getCategories()
            .filter { it.parentId == null }
            .sortedBy { it.position }
            .map { it.toDomain() }
            .map { it.toUI() }
    }
}
