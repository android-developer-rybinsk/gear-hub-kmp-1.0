package gearhub.feature.menu_feature.api

import gearhub.feature.menu_feature.internal.di.menuFeatureModule
import org.koin.core.module.Module

/**
 * Публичная точка входа фичи меню.
 */
object MenuFeatureApi {
    /**
     * Koin-модуль фичи меню.
     */
    val module: Module = menuFeatureModule
}
