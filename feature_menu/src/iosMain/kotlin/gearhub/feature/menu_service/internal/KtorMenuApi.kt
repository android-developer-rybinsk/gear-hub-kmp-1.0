package gearhub.feature.menu_service.internal

import com.gear.hub.network.config.HostProvider
import com.gear.hub.network.model.ApiResponse
import com.gear.hub.network.util.ensureTrailingSlash
import gearhub.feature.menu_feature.internal.data.models.MenuCategoryDTO
import gearhub.feature.menu_service.api.MenuApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * iOS-реализация MenuApi на базе Ktor HttpClient.
 */
class KtorMenuApi(
    private val httpClient: HttpClient,
    private val hostProvider: HostProvider,
) : MenuApi {

    override suspend fun getCategories(): ApiResponse<List<MenuCategoryDTO>> = withContext(Dispatchers.IO) {
        try {
            val response = httpClient.get(hostProvider.baseUrl().ensureTrailingSlash() + "api/v1/categories")
            return if (response.status.isSuccess()) {
                val body: List<MenuCategoryDTO> = response.body()
                ApiResponse.Success(body)
            } else {
                ApiResponse.HttpError(
                    response.status.value,
                    response.bodyAsText(),
                )
            }
        } catch (client: ClientRequestException) {
            ApiResponse.HttpError(client.response.status.value, client.message)
        } catch (server: ServerResponseException) {
            ApiResponse.HttpError(server.response.status.value, server.message)
        } catch (redirect: RedirectResponseException) {
            ApiResponse.HttpError(redirect.response.status.value, redirect.message)
        } catch (_: IOException) {
            ApiResponse.NetworkError
        } catch (throwable: Throwable) {
            ApiResponse.UnknownError(throwable)
        }
    }
}
