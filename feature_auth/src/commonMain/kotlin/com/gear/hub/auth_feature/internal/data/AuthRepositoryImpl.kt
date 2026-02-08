package com.gear.hub.auth_feature.internal.data

import com.gear.hub.auth_feature.internal.data.model.toDto
import com.gear.hub.auth_feature.internal.data.model.toDomain
import com.gear.hub.auth_feature.internal.data.session.AuthSessionStorage
import com.gear.hub.auth_feature.internal.domain.AuthRepository
import com.gear.hub.auth_feature.internal.domain.model.LoginPayload
import com.gear.hub.auth_feature.internal.domain.model.RegistrationPayload
import com.gear.hub.auth_feature.internal.domain.model.RegistrationResult
import com.gear.hub.auth_service.api.AuthApi
import com.gear.hub.network.model.ApiResponse

/**
 * Реализация репозитория авторизации через AuthApi.
 */
class AuthRepositoryImpl(
    private val api: AuthApi,
    private val sessionStorage: AuthSessionStorage,
) : AuthRepository {

    override suspend fun register(payload: RegistrationPayload): ApiResponse<RegistrationResult> {
        return when (val response = api.register(payload.toDto())) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomain())
            is ApiResponse.HttpError -> response
            ApiResponse.NetworkError -> ApiResponse.NetworkError
            is ApiResponse.UnknownError -> response
        }
    }

    override suspend fun login(payload: LoginPayload): ApiResponse<RegistrationResult> {
        val token = sessionStorage.getCredentials()?.accessToken
        val authHeader = token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
        return when (val response = api.login(payload.toDto(), authHeader)) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomain())
            is ApiResponse.HttpError -> response
            ApiResponse.NetworkError -> ApiResponse.NetworkError
            is ApiResponse.UnknownError -> response
        }
    }
}
