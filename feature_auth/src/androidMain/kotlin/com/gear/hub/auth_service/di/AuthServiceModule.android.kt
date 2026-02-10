package com.gear.hub.auth_service.di

import com.gear.hub.auth_service.api.AuthApi
import com.gear.hub.auth_service.internal.AuthRetrofitService
import com.gear.hub.auth_service.internal.RetrofitAuthApi
import com.gear.hub.network.client.NetworkClient
import com.gear.hub.network.config.HostProvider
import retrofit2.Retrofit

/**
 * Android-реализация AuthApi строится через общий Retrofit из network-service.
 */
actual fun provideAuthApi(
    defaultClient: NetworkClient,
    authorizedClient: NetworkClient,
    hostProvider: HostProvider,
): AuthApi {
    val defaultRetrofit = defaultClient as Retrofit
    val authorizedRetrofit = authorizedClient as Retrofit
    return RetrofitAuthApi(
        defaultRetrofit.create(AuthRetrofitService::class.java),
        authorizedRetrofit.create(AuthRetrofitService::class.java),
        hostProvider,
    )
}
