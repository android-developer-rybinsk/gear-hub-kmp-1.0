package com.gear.hub.auth_service.di

import com.gear.hub.auth_service.api.AuthApi
import com.gear.hub.auth_service.internal.KtorAuthApi
import com.gear.hub.network.client.NetworkClient
import com.gear.hub.network.config.HostProvider
import io.ktor.client.HttpClient

/**
 * iOS-реализация AuthApi на базе общего HttpClient.
 */
actual fun provideAuthApi(
    defaultClient: NetworkClient,
    authorizedClient: NetworkClient,
    hostProvider: HostProvider,
): AuthApi {
    val defaultHttpClient = defaultClient as HttpClient
    val authorizedHttpClient = authorizedClient as HttpClient
    return KtorAuthApi(defaultHttpClient, authorizedHttpClient, hostProvider)
}
