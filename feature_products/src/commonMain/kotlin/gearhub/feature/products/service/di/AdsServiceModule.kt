package gearhub.feature.products.service.di

import com.gear.hub.network.client.NetworkClient
import com.gear.hub.network.config.HostProvider
import gearhub.feature.products.service.api.AdsApi
import org.koin.dsl.module

/**
 * Koin-модуль сетевого слоя объявлений.
 */
val adsServiceModule = module {
    single<AdsApi> { provideAdsApi(get<NetworkClient>(), get()) }
}

/**
 * Платформенный способ получить реализацию AdsApi.
 */
expect fun provideAdsApi(client: NetworkClient, hostProvider: HostProvider): AdsApi
