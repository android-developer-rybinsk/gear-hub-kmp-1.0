package com.gear.hub.auth_service.model

import kotlinx.serialization.Serializable

/**
 * Ответ регистрации с токенами и базовой информацией пользователя.
 */
@Serializable
data class AuthRegisterResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
)
