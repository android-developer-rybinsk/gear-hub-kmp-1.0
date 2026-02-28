package gearhub.feature.menu_feature.internal.data.db

import com.gear.hub.data.config.DatabaseFactory
import gearhub.feature.menu_feature.api.db.MenuCategoryDbDriver
import gearhub.feature.menu_feature.api.models.MenuCategoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AndroidMenuCategoryDbDriver(
    factory: DatabaseFactory,
) : MenuCategoryDbDriver {

    private val database: MenuCategoryDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        factory.roomDatabaseBuilder(MenuCategoryDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()
    }

    private val dao: MenuCategoryDao by lazy(LazyThreadSafetyMode.NONE) { database.menuCategoryDao() }

    override suspend fun ensureInitialized() {
        withContext(Dispatchers.IO) { dao }
    }

    override fun setCategories(categories: List<MenuCategoryModel>) {
        dao.clear()
        dao.insertCategories(categories.map { it.toEntity() })
    }

    override fun getCategories(): List<MenuCategoryModel> =
        dao.getCategories().map { it.toRecord() }

    private fun MenuCategoryModel.toEntity(): MenuCategoryEntity = MenuCategoryEntity(
        id = id,
        slug = slug,
        name = name,
        parentId = parentId,
        position = position,
    )

    private fun MenuCategoryEntity.toRecord(): MenuCategoryModel = MenuCategoryModel(
        id = id,
        slug = slug,
        name = name,
        parentId = parentId,
        position = position,
    )
}
