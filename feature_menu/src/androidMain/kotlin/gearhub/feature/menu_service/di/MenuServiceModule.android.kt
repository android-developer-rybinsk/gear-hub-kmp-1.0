package gearhub.feature.menu_service.di

import com.gear.hub.network.client.NetworkClient
import com.gear.hub.network.config.HostProvider
import gearhub.feature.menu_service.api.MenuApi
import gearhub.feature.menu_service.internal.MenuRetrofitService
import gearhub.feature.menu_service.internal.RetrofitMenuApi
import retrofit2.Retrofit

/**
 * Android-реализация MenuApi через общий Retrofit.
 */
actual fun provideMenuApi(
    client: NetworkClient,
    hostProvider: HostProvider,
): MenuApi {
    val retrofit = client as Retrofit
    return RetrofitMenuApi(retrofit.create(MenuRetrofitService::class.java), hostProvider)
}
