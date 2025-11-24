package com.gear.hub.auth_service.internal

import com.gear.hub.auth_feature.internal.data.model.AuthRegisterRequestDto
import com.gear.hub.auth_feature.internal.data.model.AuthRegisterResponseDto
import com.gear.hub.auth_service.api.AuthApi
import com.gear.hub.network.config.HostProvider
import com.gear.hub.network.model.ApiResponse
import com.gear.hub.network.util.ensureTrailingSlash
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Реализация AuthApi для iOS на Ktor HttpClient.
 */
class KtorAuthApi(
    private val httpClient: HttpClient,
    private val hostProvider: HostProvider,
) : AuthApi {

    override suspend fun register(request: AuthRegisterRequestDto): ApiResponse<AuthRegisterResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = httpClient.post(hostProvider.baseUrl().ensureTrailingSlash() + "api/v1/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            val body: AuthRegisterResponseDto = response.body()
            if (response.status.isSuccess()) {
                ApiResponse.Success(body)
            } else {
                ApiResponse.HttpError(response.status.value, response.status.description)
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
