package com.gear.hub.data.config

/**
 * Заглушка для iOS: контекст не требуется, но поддерживается общим интерфейсом.
 */
actual class PlatformContext actual constructor()

/**
 * Runtime для iOS: пока содержит только конфигурацию, чтобы фичи могли подключить свой storage.
 */
actual class DatabaseRuntime(val config: DatabaseConfig)

/**
 * Фабрика для iOS: прокидывает конфиг в инициализаторы фич.
 */
actual class EncryptedDatabaseFactory actual constructor(platformContext: PlatformContext) {
    actual fun initialize(config: DatabaseConfig, registry: DatabaseRegistry) {
        val runtime = DatabaseRuntime(config)
        registry.registeredModules.values.forEach { initializer -> initializer.invoke(runtime) }
    }
}
