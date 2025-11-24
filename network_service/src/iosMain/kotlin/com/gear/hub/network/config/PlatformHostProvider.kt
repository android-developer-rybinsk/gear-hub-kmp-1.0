package com.gear.hub.network.config

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * iOS-реализация провайдера хоста: значения хостов передаются снаружи (например, из настроек стенда).
 */
actual class PlatformHostProvider actual constructor(
    defaultEnv: Environment,
    private val defaultDevHost: String,
    private val defaultProdHost: String
) : HostProvider {
    private val environmentState = MutableStateFlow(defaultEnv)
    override val environment: StateFlow<Environment> = environmentState

    override fun baseUrl(): String {
        return when (environmentState.value) {
            Environment.DEV -> defaultDevHost
            Environment.PROD -> defaultProdHost
        }
    }

    override fun setEnvironment(target: Environment) {
        environmentState.value = target
    }
}
