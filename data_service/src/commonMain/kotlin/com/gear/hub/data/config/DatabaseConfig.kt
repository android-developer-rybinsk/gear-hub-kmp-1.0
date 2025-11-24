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
 * Объект окружения, передаваемый фичевым инициализаторам для создания своих таблиц/DAO.
 */
expect class DatabaseRuntime

/**
 * Реестр инициализаторов БД: каждая фича регистрирует свой модуль с таблицами.
 */
interface DatabaseRegistry {
    /**
     * Регистрирует инициализатор БД конкретной фичи.
     */
    fun registerModule(moduleName: String, initializer: (DatabaseRuntime) -> Unit)

    /**
     * Зарегистрированные инициализаторы по имени модуля.
     */
    val registeredModules: Map<String, (DatabaseRuntime) -> Unit>
}

/**
 * Базовая реализация реестра инициализаторов.
 */
class DefaultDatabaseRegistry : DatabaseRegistry {
    private val modules = linkedMapOf<String, (DatabaseRuntime) -> Unit>()

    override fun registerModule(moduleName: String, initializer: (DatabaseRuntime) -> Unit) {
        modules[moduleName] = initializer
    }

    override val registeredModules: Map<String, (DatabaseRuntime) -> Unit> get() = modules
}

/**
 * Фабрика шифрованной БД: готовит платформенный runtime и отдает его в модульные инициализаторы.
 */
expect class EncryptedDatabaseFactory(platformContext: PlatformContext) {
    /**
     * Создаёт шифрованный runtime и отдает его в модульные инициализаторы,
     * возвращая готовый экземпляр для дальнейшей работы фич.
     */
    fun initialize(config: DatabaseConfig, registry: DatabaseRegistry): DatabaseRuntime
}

/**
 * Платформенный контейнер контекста приложения (Android Context / iOS объект окружения).
 * Для Android внутри хранится `Context`, для iOS может быть `null`, так как он не требуется.
 */
expect class PlatformContext constructor(platformValue: Any?)
