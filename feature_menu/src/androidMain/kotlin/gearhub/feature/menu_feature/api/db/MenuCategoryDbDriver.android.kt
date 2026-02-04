package gearhub.feature.menu_feature.api.db

import com.gear.hub.data.config.DatabaseFactory
import gearhub.feature.menu_feature.internal.data.db.AndroidMenuCategoryDbDriver

/**
 * Android-реализация драйвера БД категорий меню.
 */
actual fun createMenuCategoryDbDriver(factory: DatabaseFactory): MenuCategoryDbDriver =
    AndroidMenuCategoryDbDriver(factory)
