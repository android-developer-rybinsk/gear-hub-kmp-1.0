package com.gear.hub.auth_service.di

import com.gear.hub.auth_service.api.AuthApi
import com.gear.hub.auth_service.internal.KtorAuthApi
import com.gear.hub.network.config.HostProvider
import io.ktor.client.HttpClient
import org.koin.core.scope.get

/**
 * iOS-реализация AuthApi на базе общего HttpClient.
 */
actual fun provideAuthApi(): AuthApi {
    val scope = org.koin.core.context.GlobalContext.get().scopeRegistry.rootScope
    val httpClient: HttpClient = scope.get()
    val hostProvider: HostProvider = scope.get()
    return KtorAuthApi(httpClient, hostProvider)
}
