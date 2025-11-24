package com.gear.hub.auth_feature.internal.domain

import com.gear.hub.auth_feature.internal.domain.model.RegistrationPayload
import com.gear.hub.auth_feature.internal.domain.model.RegistrationResult
import com.gear.hub.network.model.ApiResponse

/**
 * Репозиторий авторизации: объединяет сетевой слой и доменную логику валидации.
 */
interface AuthRepository {
    /**
     * Выполнить регистрацию на бэкенде.
     */
    suspend fun register(payload: RegistrationPayload): ApiResponse<RegistrationResult>
}
