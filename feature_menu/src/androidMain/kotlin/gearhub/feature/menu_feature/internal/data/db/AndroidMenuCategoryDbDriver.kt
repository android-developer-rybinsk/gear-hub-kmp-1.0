package gearhub.feature.menu_feature.internal.data.db

import com.gear.hub.data.config.DatabaseFactory
import gearhub.feature.menu_feature.api.db.MenuCategoryDbDriver
import gearhub.feature.menu_feature.api.model.MenuCategoryRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AndroidMenuCategoryDbDriver(
    factory: DatabaseFactory,
) : MenuCategoryDbDriver {

    private val database: MenuCategoryDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        factory.roomDatabaseBuilder(MenuCategoryDatabase::class.java)
            .build()
    }

    private val dao: MenuCategoryDao by lazy(LazyThreadSafetyMode.NONE) { database.menuCategoryDao() }

    override suspend fun ensureInitialized() {
        withContext(Dispatchers.IO) { dao }
    }

    override fun setCategories(categories: List<MenuCategoryRecord>) {
        dao.clear()
        dao.insertCategories(categories.map { it.toEntity() })
    }

    override fun getCategories(): List<MenuCategoryRecord> =
        dao.getCategories().map { it.toRecord() }

    private fun MenuCategoryRecord.toEntity(): MenuCategoryEntity = MenuCategoryEntity(
        id = id,
        slug = slug,
        name = name,
        parentId = parentId,
        position = position,
    )

    private fun MenuCategoryEntity.toRecord(): MenuCategoryRecord = MenuCategoryRecord(
        id = id,
        slug = slug,
        name = name,
        parentId = parentId,
        position = position,
    )
}
