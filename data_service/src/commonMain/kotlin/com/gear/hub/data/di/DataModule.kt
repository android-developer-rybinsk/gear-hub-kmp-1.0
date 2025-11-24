package com.gear.hub.data.di

import com.gear.hub.data.config.DatabaseConfig
import com.gear.hub.data.config.DatabaseRegistry
import com.gear.hub.data.config.DefaultDatabaseRegistry
import com.gear.hub.data.config.EncryptedDatabaseFactory
import com.gear.hub.data.config.PlatformContext
import org.koin.dsl.module

/**
 * Базовый модуль Koin для data_service: регистрирует фабрику БД и реестр инициализаторов.
 */
fun dataModule(
    config: DatabaseConfig,
    platformContext: PlatformContext,
    registryConfig: (DatabaseRegistry.() -> Unit)? = null,
) = module {
    single<DatabaseRegistry> { DefaultDatabaseRegistry().apply { registryConfig?.invoke(this) } }
    single { EncryptedDatabaseFactory(platformContext) }
    single { get<EncryptedDatabaseFactory>().initialize(config, get()) }
}
