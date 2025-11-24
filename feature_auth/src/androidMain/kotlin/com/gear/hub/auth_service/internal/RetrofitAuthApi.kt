package com.gear.hub.auth_service.internal

import com.gear.hub.auth_feature.internal.data.model.AuthRegisterRequestDto
import com.gear.hub.auth_feature.internal.data.model.AuthRegisterResponseDto
import com.gear.hub.auth_service.api.AuthApi
import com.gear.hub.network.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Android-реализация AuthApi на базе общего Retrofit-клиента.
 */
class RetrofitAuthApi(
    private val service: AuthRetrofitService,
) : AuthApi {

    override suspend fun register(request: AuthRegisterRequestDto): ApiResponse<AuthRegisterResponseDto> =
        try {
            val response = service.register(request)
            ApiResponse.Success(response)
        } catch (http: retrofit2.HttpException) {
            val message = http.response()?.errorBody()?.string()
            ApiResponse.HttpError(http.code(), message)
        } catch (_: java.io.IOException) {
            ApiResponse.NetworkError
        } catch (throwable: Throwable) {
            ApiResponse.UnknownError(throwable)
        }

}

/**
 * Retrofit-интерфейс регистрационного эндпоинта.
 */
internal interface AuthRetrofitService {
    @POST("api/v1/auth/register")
    suspend fun register(@Body body: AuthRegisterRequestDto): AuthRegisterResponseDto
}
