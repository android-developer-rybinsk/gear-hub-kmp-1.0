package gearhub.feature.products.product_feature.internal.di

import gearhub.feature.products.product_feature.internal.data.AdsRepositoryImpl
import gearhub.feature.products.product_feature.internal.domain.AdsRepository
import gearhub.feature.products.product_service.di.adsServiceModule
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin-модуль фичи создания объявлений.
 */
val productFeatureModule: Module = module {
    includes(adsServiceModule)

    single<AdsRepository> { AdsRepositoryImpl(get()) }
}
