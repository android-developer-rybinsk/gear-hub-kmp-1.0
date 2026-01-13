package com.gear.hub.network.config

import kotlinx.coroutines.flow.StateFlow

/**
 * Провайдер хоста с возможностью переключения окружений (dev/prod) в рантайме.
 */
interface HostProvider {
    /**
     * Текущее окружение, на которое заведен host (для отображения в дебаг-меню).
     */
    val environment: StateFlow<Environment>

    /**
     * Возвращает базовый url для текущего окружения.
     */
    fun baseUrl(): String

    /**
     * Переключает окружение.
     */
    fun setEnvironment(target: Environment)
}

/**
 * Платформенная реализация провайдера хоста (Android/iOS), которая читает значения из BuildConfig/параметров.
 */
expect class PlatformHostProvider(defaultEnv: Environment, defaultDevHost: String, defaultProdHost: String) : HostProvider
