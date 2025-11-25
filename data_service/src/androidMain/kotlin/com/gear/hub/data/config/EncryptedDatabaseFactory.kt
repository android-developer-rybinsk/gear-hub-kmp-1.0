package com.gear.hub.data.config

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SupportFactory

/**
 * Платформенный контейнер Android: хранит Context для создания базы данных.
 */
actual class PlatformContext actual constructor(val platformValue: Any?) {
    val context: Context = platformValue as Context
}

/**
 * Платформенная фабрика для Room/SQLCipher, подготавливающая базовый билдер.
 */
actual class DatabaseFactory(
    val context: PlatformContext,
    val config: DatabaseConfig,
    private val passphrase: ByteArray,
) {
    /**
     * Создаёт Room-билдер с подключённым SQLCipher, чтобы фичи могли собирать свои базы.
     */
    fun <T : RoomDatabase> roomDatabaseBuilder(dbClass: Class<T>): RoomDatabase.Builder<T> {
        val supportFactory = SupportFactory(passphrase.copyOf())
        return Room.databaseBuilder(context.context, dbClass, config.name)
            .openHelperFactory(supportFactory)
    }
}

/**
 * Фабрика, подготавливающая шифрованный контейнер БД и отдающая его в фичевые инициализаторы.
 */
actual class EncryptedDatabaseFactory actual constructor(private val platformContext: PlatformContext) {
    actual fun initialize(config: DatabaseConfig, registry: DatabaseRegistry): DatabaseFactory {
        val passphrase = config.passphrase.toByteArray()
        val factory = DatabaseFactory(
            context = platformContext,
            config = config,
            passphrase = passphrase,
        )
        registry.registeredModules.values.forEach { initializer -> initializer.invoke(factory) }
        return factory
    }
}
