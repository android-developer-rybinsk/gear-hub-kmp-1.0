package com.gear.hub.auth_feature.internal.domain

import com.gear.hub.auth_service.model.AuthRegisterRequest
import com.gear.hub.auth_service.model.AuthRegisterResponse
import com.gear.hub.network.model.ApiResponse

/**
 * Репозиторий авторизации: объединяет сетевой слой и доменную логику валидации.
 */
interface AuthRepository {
    /**
     * Выполнить регистрацию на бэкенде.
     */
    suspend fun register(request: AuthRegisterRequest): ApiResponse<AuthRegisterResponse>
}
