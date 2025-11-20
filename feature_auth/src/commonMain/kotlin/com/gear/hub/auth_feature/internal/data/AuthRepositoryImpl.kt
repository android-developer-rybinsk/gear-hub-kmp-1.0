package com.gear.hub.auth_feature.internal.data

import com.gear.hub.auth_feature.internal.domain.AuthRepository
import com.gear.hub.auth_service.api.AuthApi
import com.gear.hub.auth_service.model.AuthRegisterRequest
import com.gear.hub.auth_service.model.AuthRegisterResponse
import com.gear.hub.network.model.ApiResponse

/**
 * Реализация репозитория авторизации через AuthApi.
 */
class AuthRepositoryImpl(
    private val api: AuthApi,
) : AuthRepository {

    override suspend fun register(request: AuthRegisterRequest): ApiResponse<AuthRegisterResponse> = api.register(request)
}
