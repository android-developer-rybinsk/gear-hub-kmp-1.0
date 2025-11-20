package com.gear.hub.auth_feature.api

import com.gear.hub.auth_feature.internal.di.authFeatureModule
import org.koin.core.module.Module

/**
 * Публичная точка входа фичи авторизации: набор зависимостей и навигационные константы.
 */
object AuthFeatureApi {
    /**
     * Koin-модуль, который нужно подключить в приложении.
     */
    val module: Module = authFeatureModule
}
