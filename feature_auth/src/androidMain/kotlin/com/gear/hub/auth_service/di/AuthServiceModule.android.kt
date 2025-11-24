package com.gear.hub.auth_service.di

import com.gear.hub.auth_service.api.AuthApi
import com.gear.hub.auth_service.internal.AuthRetrofitService
import com.gear.hub.auth_service.internal.RetrofitAuthApi
import retrofit2.Retrofit

/**
 * Android-реализация AuthApi строится через общий Retrofit из network-service.
 */
actual fun provideAuthApi(client: Any): AuthApi {
    val retrofit = client as Retrofit
    return RetrofitAuthApi(retrofit.create(AuthRetrofitService::class.java))
}
