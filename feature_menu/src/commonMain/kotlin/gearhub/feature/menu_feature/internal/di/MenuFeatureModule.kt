package gearhub.feature.menu_feature.internal.di

import com.gear.hub.data.config.DatabaseFactory
import gearhub.feature.menu_feature.api.db.MenuCategoryDbDriver
import gearhub.feature.menu_feature.api.db.createMenuCategoryDbDriver
import gearhub.feature.menu_feature.api.MenuCategoryProvider
import gearhub.feature.menu_feature.internal.data.MenuCategoryRepository
import gearhub.feature.menu_feature.internal.data.MenuCategoryRepositoryImpl
import gearhub.feature.menu_feature.internal.data.MenuCategoryProviderImpl
import gearhub.feature.menu_service.di.menuServiceModule
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Внутренний модуль Koin для фичи меню.
 */
val menuFeatureModule: Module = module {
    includes(menuServiceModule)

    single<MenuCategoryDbDriver> { createMenuCategoryDbDriver(get<DatabaseFactory>(named("menu_db"))) }
    single<MenuCategoryRepository> { MenuCategoryRepositoryImpl(get(), get()) }
    single<MenuCategoryProvider> { MenuCategoryProviderImpl(get()) }
}
