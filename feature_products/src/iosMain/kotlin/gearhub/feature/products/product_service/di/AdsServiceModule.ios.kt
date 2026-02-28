package gearhub.feature.products.product_service.di

import com.gear.hub.network.client.NetworkClient
import com.gear.hub.network.config.HostProvider
import gearhub.feature.products.product_service.api.AdsApi
import gearhub.feature.products.product_service.internal.KtorAdsApi
import io.ktor.client.HttpClient

/**
 * iOS-реализация AdsApi через Ktor.
 */
actual fun provideAdsApi(
    client: NetworkClient,
    hostProvider: HostProvider,
): AdsApi {
    val httpClient = client as HttpClient
    return KtorAdsApi(httpClient, hostProvider)
}
