package com.gear.hub.data.di

import com.gear.hub.data.config.DatabaseConfig
import com.gear.hub.data.config.DatabaseRegistry
import com.gear.hub.data.config.DefaultDatabaseRegistry
import com.gear.hub.data.config.EncryptedDatabaseFactory
import com.gear.hub.data.config.PlatformContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Базовый модуль Koin для data_service: регистрирует фабрику БД и реестр инициализаторов.
 */
fun dataModule(
    config: DatabaseConfig,
    platformContext: PlatformContext,
    registryConfig: (DatabaseRegistry.() -> Unit)? = null,
    qualifier: String? = null,
) = module {
    val namedQualifier = qualifier?.let { named(it) }
    if (namedQualifier == null) {
        single<DatabaseRegistry> { DefaultDatabaseRegistry().apply { registryConfig?.invoke(this) } }
        single { EncryptedDatabaseFactory(platformContext) }
        single { get<EncryptedDatabaseFactory>().initialize(config, get()) }
    } else {
        single<DatabaseRegistry>(namedQualifier) { DefaultDatabaseRegistry().apply { registryConfig?.invoke(this) } }
        single(namedQualifier) { EncryptedDatabaseFactory(platformContext) }
        single(namedQualifier) { get<EncryptedDatabaseFactory>(namedQualifier).initialize(config, get(namedQualifier)) }
    }
}
