package com.gear.hub.auth_service.api

import com.gear.hub.auth_service.model.AuthRegisterRequest
import com.gear.hub.auth_service.model.AuthRegisterResponse
import com.gear.hub.network.model.ApiResponse

/**
 * Контракт сетевых вызовов авторизации/регистрации.
 */
interface AuthApi {
    /**
     * Регистрация нового пользователя.
     */
    suspend fun register(request: AuthRegisterRequest): ApiResponse<AuthRegisterResponse>
}

