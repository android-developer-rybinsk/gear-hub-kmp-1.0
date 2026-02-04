package gearhub.feature.menu_feature.api.db

import com.gear.hub.data.config.DatabaseFactory
import gearhub.feature.menu_feature.internal.data.db.IosMenuCategoryDbDriver

/**
 * iOS-реализация драйвера БД категорий меню.
 */
actual fun createMenuCategoryDbDriver(factory: DatabaseFactory): MenuCategoryDbDriver =
    IosMenuCategoryDbDriver(factory)
