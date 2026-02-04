package gearhub.feature.menu_feature.api.db

import com.gear.hub.data.config.DatabaseFactory
import gearhub.feature.menu_feature.api.model.MenuCategoryRecord

/**
 * Контракт доступа к таблице категорий меню.
 */
interface MenuCategoryDbDriver {
    /**
     * Инициализирует таблицы при необходимости.
     */
    suspend fun ensureInitialized()

    /**
     * Сохраняет список категорий.
     */
    fun setCategories(categories: List<MenuCategoryRecord>)

    /**
     * Возвращает список сохранённых категорий.
     */
    fun getCategories(): List<MenuCategoryRecord>
}

/**
 * Фабричная функция создания драйвера БД на платформе.
 */
expect fun createMenuCategoryDbDriver(factory: DatabaseFactory): MenuCategoryDbDriver
