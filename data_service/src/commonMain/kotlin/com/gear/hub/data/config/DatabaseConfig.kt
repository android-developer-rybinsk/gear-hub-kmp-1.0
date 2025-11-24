package com.gear.hub.data.config

/**
 * Конфигурация шифрованной базы данных.
 */
data class DatabaseConfig(
    val name: String,
    val passphrase: String,
    val version: Int = 1,
)

/**
 * Фабрика платформенной БД, передаваемая в фичевые инициализаторы для создания своих таблиц/DAO.
 */
expect class DatabaseFactory

/**
 * Реестр инициализаторов БД: каждая фича регистрирует свой модуль с таблицами.
 */
interface DatabaseRegistry {
    /**
     * Регистрирует инициализатор БД конкретной фичи.
     */
    fun registerModule(moduleName: String, initializer: (DatabaseFactory) -> Unit)

    /**
     * Зарегистрированные инициализаторы по имени модуля.
     */
    val registeredModules: Map<String, (DatabaseFactory) -> Unit>
}

/**
 * Базовая реализация реестра инициализаторов.
 */
class DefaultDatabaseRegistry : DatabaseRegistry {
    private val modules = linkedMapOf<String, (DatabaseFactory) -> Unit>()

    override fun registerModule(moduleName: String, initializer: (DatabaseFactory) -> Unit) {
        modules[moduleName] = initializer
    }

    override val registeredModules: Map<String, (DatabaseFactory) -> Unit> get() = modules
}

/**
 * Фабрика шифрованной БД: готовит платформенный runtime и отдает его в модульные инициализаторы.
 */
expect class EncryptedDatabaseFactory(platformContext: PlatformContext) {
    /**
     * Создаёт шифрованный runtime и отдает его в модульные инициализаторы,
     * возвращая готовый экземпляр для дальнейшей работы фич.
     */
    fun initialize(config: DatabaseConfig, registry: DatabaseRegistry): DatabaseFactory
}

/**
 * Платформенный контейнер контекста приложения (Android Context / iOS объект окружения).
 * Для Android внутри хранится `Context`, для iOS может быть `null`, так как он не требуется.
 */
expect class PlatformContext constructor(platformValue: Any?)
