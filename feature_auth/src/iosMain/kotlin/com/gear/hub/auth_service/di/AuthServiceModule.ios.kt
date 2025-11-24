package com.gear.hub.auth_service.di

import com.gear.hub.auth_service.api.AuthApi
import com.gear.hub.auth_service.internal.KtorAuthApi
import com.gear.hub.network.config.HostProvider
import io.ktor.client.HttpClient

/**
 * iOS-реализация AuthApi на базе общего HttpClient.
 */
actual fun provideAuthApi(
    client: Any,
    hostProvider: HostProvider,
): AuthApi {
    val httpClient = client as HttpClient
    return KtorAuthApi(httpClient, hostProvider)
}
