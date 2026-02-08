package gearhub.feature.products.product_service.internal

import com.gear.hub.network.config.HostProvider
import com.gear.hub.network.model.ApiResponse
import com.gear.hub.network.util.ensureTrailingSlash
import gearhub.feature.products.product_feature.internal.data.models.CreateAdRequestDTO
import gearhub.feature.products.product_feature.internal.data.models.CreateAdResponseDTO
import gearhub.feature.products.product_feature.internal.data.models.UpdateAdRequestDTO
import gearhub.feature.products.product_service.api.AdsApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Реализация AdsApi для iOS на Ktor HttpClient.
 */
class KtorAdsApi(
    private val httpClient: HttpClient,
    private val hostProvider: HostProvider,
) : AdsApi {

    override suspend fun createAd(request: CreateAdRequestDTO): ApiResponse<CreateAdResponseDTO> = withContext(Dispatchers.IO) {
        try {
            val response = httpClient.post(hostProvider.baseUrl().ensureTrailingSlash() + "api/v1/ads") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            return if (response.status.isSuccess()) {
                val body: CreateAdResponseDTO = response.body()
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

    override suspend fun updateAd(id: String, request: UpdateAdRequestDTO): ApiResponse<CreateAdResponseDTO> = withContext(Dispatchers.IO) {
        try {
            val response = httpClient.patch(hostProvider.baseUrl().ensureTrailingSlash() + "api/v1/ads/$id") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            return if (response.status.isSuccess()) {
                val body: CreateAdResponseDTO = response.body()
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
