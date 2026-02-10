package com.gear.hub.auth_service.api

import com.gear.hub.auth_feature.internal.data.model.AuthRegisterRequestDto
import com.gear.hub.auth_feature.internal.data.model.AuthRegisterResponseDto
import com.gear.hub.auth_feature.internal.data.model.AuthLoginRequestDto
import com.gear.hub.auth_feature.internal.data.model.AuthRefreshRequestDto
import com.gear.hub.auth_feature.internal.data.model.AuthRefreshResponseDto
import com.gear.hub.network.model.ApiResponse

/**
 * Контракт сетевых вызовов авторизации/регистрации.
 */
interface AuthApi {
    /**
     * Регистрация нового пользователя.
     */
    suspend fun register(request: AuthRegisterRequestDto): ApiResponse<AuthRegisterResponseDto>

    /**
     * Логин пользователя.
     */
    suspend fun login(
        request: AuthLoginRequestDto,
    ): ApiResponse<AuthRegisterResponseDto>

    /**
     * Обновление access token по refresh token.
     */
    suspend fun refresh(
        request: AuthRefreshRequestDto,
    ): ApiResponse<AuthRefreshResponseDto>
}
