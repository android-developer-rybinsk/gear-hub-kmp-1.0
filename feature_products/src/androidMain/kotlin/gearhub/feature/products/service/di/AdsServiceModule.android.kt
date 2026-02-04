package gearhub.feature.products.service.di

import com.gear.hub.network.client.NetworkClient
import com.gear.hub.network.config.HostProvider
import gearhub.feature.products.service.api.AdsApi
import gearhub.feature.products.service.internal.AdsRetrofitService
import gearhub.feature.products.service.internal.RetrofitAdsApi
import retrofit2.Retrofit

/**
 * Android-реализация AdsApi через общий Retrofit.
 */
actual fun provideAdsApi(
    client: NetworkClient,
    hostProvider: HostProvider,
): AdsApi {
    val retrofit = client as Retrofit
    return RetrofitAdsApi(retrofit.create(AdsRetrofitService::class.java), hostProvider)
}
