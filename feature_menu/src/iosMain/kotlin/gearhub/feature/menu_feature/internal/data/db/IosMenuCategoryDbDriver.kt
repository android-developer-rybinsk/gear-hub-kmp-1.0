package gearhub.feature.menu_feature.internal.data.db

import com.gear.hub.data.config.DatabaseFactory
import gearhub.feature.menu_feature.api.db.MenuCategoryDbDriver
import gearhub.feature.menu_feature.api.models.MenuCategoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

internal class IosMenuCategoryDbDriver(
    factory: DatabaseFactory,
) : MenuCategoryDbDriver {

    private val defaults = NSUserDefaults.standardUserDefaults
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun ensureInitialized() {
        withContext(Dispatchers.Default) { defaults }
    }

    override fun setCategories(categories: List<MenuCategoryModel>) {
        val payload = json.encodeToString(categories)
        defaults.setObject(payload, CATEGORIES_KEY)
    }

    override fun getCategories(): List<MenuCategoryModel> {
        val payload = defaults.stringForKey(CATEGORIES_KEY) ?: return emptyList()
        return runCatching { json.decodeFromString<List<MenuCategoryModel>>(payload) }
            .getOrDefault(emptyList())
    }

    private companion object {
        const val CATEGORIES_KEY = "menu.categories"
    }
}
