package gearhub.feature.products.product_service.di

import com.gear.hub.network.client.NetworkClient
import com.gear.hub.network.client.NetworkClientQualifiers
import com.gear.hub.network.config.HostProvider
import gearhub.feature.products.product_service.api.AdsApi
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin-модуль сетевого слоя объявлений.
 */
val adsServiceModule = module {
    single<AdsApi> { provideAdsApi(get(named(NetworkClientQualifiers.AUTHORIZED)), get()) }
}

/**
 * Платформенный способ получить реализацию AdsApi.
 */
expect fun provideAdsApi(client: NetworkClient, hostProvider: HostProvider): AdsApi
