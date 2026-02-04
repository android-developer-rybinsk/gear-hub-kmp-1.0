package gearhub.feature.products.internal.di

import gearhub.feature.products.internal.data.AdsRepository
import gearhub.feature.products.internal.data.AdsRepositoryImpl
import gearhub.feature.products.service.di.adsServiceModule
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin-модуль фичи создания объявлений.
 */
val adsFeatureModule: Module = module {
    includes(adsServiceModule)

    single<AdsRepository> { AdsRepositoryImpl(get()) }
}
