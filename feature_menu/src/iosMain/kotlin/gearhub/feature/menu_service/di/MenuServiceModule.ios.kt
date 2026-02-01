package gearhub.feature.menu_service.di

import com.gear.hub.network.client.NetworkClient
import com.gear.hub.network.config.HostProvider
import gearhub.feature.menu_service.api.MenuApi
import gearhub.feature.menu_service.internal.KtorMenuApi
import io.ktor.client.HttpClient

/**
 * iOS-реализация MenuApi на базе общего HttpClient.
 */
actual fun provideMenuApi(
    client: NetworkClient,
    hostProvider: HostProvider,
): MenuApi {
    val httpClient = client as HttpClient
    return KtorMenuApi(httpClient, hostProvider)
}
