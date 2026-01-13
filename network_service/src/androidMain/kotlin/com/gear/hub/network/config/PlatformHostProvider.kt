package com.gear.hub.network.config

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Android-реализация провайдера хоста: принимает dev/prod хосты из конфигурации
 * и позволяет переключаться между ними в рантайме.
 */
actual class PlatformHostProvider actual constructor(
    defaultEnv: Environment,
    private val defaultDevHost: String,
    private val defaultProdHost: String
) : HostProvider {

    private val environmentState = MutableStateFlow(defaultEnv)
    override val environment: StateFlow<Environment> = environmentState

    override fun baseUrl(): String = when (environmentState.value) {
        Environment.DEV -> defaultDevHost
        Environment.PROD -> defaultProdHost
    }

    override fun setEnvironment(target: Environment) {
        environmentState.value = target
    }
}
