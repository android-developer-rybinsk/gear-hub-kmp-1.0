package com.gear.hub.network.platform

import com.gear.hub.network.BuildConfig
import com.gear.hub.network.config.Environment
import com.gear.hub.network.config.HostProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Android-реализация провайдера хоста: читает dev/prod хосты из BuildConfig и позволяет
 * переключаться между ними в рантайме.
 */
actual class PlatformHostProvider actual constructor(
    defaultEnv: Environment,
    private val defaultDevHost: String,
    private val defaultProdHost: String
) : HostProvider {

    private val environmentState = MutableStateFlow(defaultEnv)
    override val environment: StateFlow<Environment> = environmentState

    override fun baseUrl(): String {
        val dev = defaultDevHost.ifBlank { BuildConfig.DEV_HOST }
        val prod = defaultProdHost.ifBlank { BuildConfig.PROD_HOST }
        return when (environmentState.value) {
            Environment.DEV -> dev
            Environment.PROD -> prod
        }
    }

    override fun setEnvironment(target: Environment) {
        environmentState.value = target
    }
}
