package gearhub.feature.menu_service.di

import com.gear.hub.network.client.NetworkClient
import com.gear.hub.network.client.NetworkClientQualifiers
import com.gear.hub.network.config.HostProvider
import gearhub.feature.menu_service.api.MenuApi
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin-модуль сетевого слоя меню.
 */
val menuServiceModule = module {
    single<MenuApi> { provideMenuApi(get(named(NetworkClientQualifiers.AUTHORIZED)), get()) }
}

/**
 * Платформенный способ получить реализацию MenuApi.
 */
expect fun provideMenuApi(client: NetworkClient, hostProvider: HostProvider): MenuApi
