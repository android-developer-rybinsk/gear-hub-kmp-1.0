package gearhub.feature.products.product_service.internal

import com.gear.hub.network.config.HostProvider
import com.gear.hub.network.model.ApiResponse
import com.gear.hub.network.util.ensureTrailingSlash
import gearhub.feature.products.product_feature.internal.data.models.AdsPageResponseDTO
import gearhub.feature.products.product_feature.internal.data.models.AdsSaveRequestDataModel
import gearhub.feature.products.product_feature.internal.data.models.AdsSaveResponseDataModel
import gearhub.feature.products.product_feature.internal.data.models.AdsWizardRequestDataModel
import gearhub.feature.products.product_feature.internal.data.models.AdsWizardResponseDataModel
import gearhub.feature.products.product_feature.internal.data.models.CreateAdRequestDTO
import gearhub.feature.products.product_feature.internal.data.models.CreateAdResponseDTO
import gearhub.feature.products.product_feature.internal.data.models.UpdateAdRequestDTO
import gearhub.feature.products.product_feature.internal.data.models.ProductCategoryDTO
import gearhub.feature.products.product_service.api.AdsApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KtorAdsApi(
    private val httpClient: HttpClient,
    private val hostProvider: HostProvider,
) : AdsApi {

    override suspend fun getCategories(): ApiResponse<List<ProductCategoryDTO>> = withContext(Dispatchers.IO) {
        try {
            val response = httpClient.get(hostProvider.baseUrl().ensureTrailingSlash() + "api/v1/categories")
            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body())
            } else {
                ApiResponse.HttpError(response.status.value, response.bodyAsText())
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

    override suspend fun getAds(limit: Int, cursor: String?): ApiResponse<AdsPageResponseDTO> = withContext(Dispatchers.IO) {
        try {
            val response = httpClient.get(hostProvider.baseUrl().ensureTrailingSlash() + "api/v1/ads") {
                parameter("limit", limit)
                cursor?.let { parameter("cursor", it) }
            }
            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body())
            } else {
                ApiResponse.HttpError(response.status.value, response.bodyAsText())
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

    override suspend fun wizard(request: AdsWizardRequestDataModel): ApiResponse<AdsWizardResponseDataModel> =
        postCall("api/v1/ads/wizard", request)

    override suspend fun save(request: AdsSaveRequestDataModel): ApiResponse<AdsSaveResponseDataModel> =
        postCall("api/v1/ads/save", request)

    override suspend fun createAd(request: CreateAdRequestDTO): ApiResponse<CreateAdResponseDTO> =
        postCall("api/v1/ads", request)

    override suspend fun updateAd(id: String, request: UpdateAdRequestDTO): ApiResponse<CreateAdResponseDTO> = withContext(Dispatchers.IO) {
        try {
            val response = httpClient.patch(hostProvider.baseUrl().ensureTrailingSlash() + "api/v1/ads/$id") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body())
            } else {
                ApiResponse.HttpError(response.status.value, response.bodyAsText())
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

    private suspend inline fun <reified Req : Any, reified Res : Any> postCall(
        path: String,
        request: Req,
    ): ApiResponse<Res> = withContext(Dispatchers.IO) {
        try {
            val response = httpClient.post(hostProvider.baseUrl().ensureTrailingSlash() + path) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body())
            } else {
                ApiResponse.HttpError(response.status.value, response.bodyAsText())
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
