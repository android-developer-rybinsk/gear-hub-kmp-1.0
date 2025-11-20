package com.gear.hub.auth_service.di

import com.gear.hub.auth_service.api.AuthApi
import com.gear.hub.auth_service.internal.AuthRetrofitService
import com.gear.hub.auth_service.internal.RetrofitAuthApi
import org.koin.core.scope.get
import retrofit2.Retrofit

/**
 * Android-реализация AuthApi строится через общий Retrofit из network-service.
 */
actual fun provideAuthApi(): AuthApi {
    val scope = org.koin.core.context.GlobalContext.get().scopeRegistry.rootScope
    val retrofit: Retrofit = scope.get()
    return RetrofitAuthApi(retrofit.create(AuthRetrofitService::class.java))
}
