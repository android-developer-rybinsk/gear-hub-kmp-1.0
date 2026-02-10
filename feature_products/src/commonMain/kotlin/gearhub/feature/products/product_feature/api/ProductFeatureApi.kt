package gearhub.feature.products.product_feature.api

import gearhub.feature.products.product_feature.internal.di.productFeatureModule
import org.koin.core.module.Module

/**
 * Публичная точка входа фичи продуктов.
 */
object ProductFeatureApi {
    /**
     * Koin-модуль фичи продуктов.
     */
    val module: Module = productFeatureModule
}
