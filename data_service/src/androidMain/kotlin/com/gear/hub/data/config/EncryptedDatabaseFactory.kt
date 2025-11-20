package com.gear.hub.data.config

import net.sqlcipher.database.SupportFactory
import kotlin.ByteArray

actual typealias PlatformContext = android.content.Context

/**
 * Платформенный runtime для Android с SupportFactory SQLCipher.
 */
actual class DatabaseRuntime(
    val context: PlatformContext,
    val config: DatabaseConfig,
    val supportFactory: SupportFactory,
)

/**
 * Фабрика, подготавливающая шифрованный runtime и отдающая его в фичевые инициализаторы.
 */
actual class EncryptedDatabaseFactory actual constructor(private val platformContext: PlatformContext) {
    actual fun initialize(config: DatabaseConfig, registry: DatabaseRegistry) {
        val passphrase: ByteArray = config.passphrase.toByteArray()
        val runtime = DatabaseRuntime(
            context = platformContext,
            config = config,
            supportFactory = SupportFactory(passphrase),
        )
        registry.registeredModules.values.forEach { initializer -> initializer.invoke(runtime) }
    }
}
