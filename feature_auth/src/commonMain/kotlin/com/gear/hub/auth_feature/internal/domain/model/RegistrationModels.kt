package com.gear.hub.auth_feature.internal.domain.model

/**
 * Данные для регистрации пользователя в доменном слое.
 */
data class RegistrationPayload(
    val name: String,
    val emailOrPhone: String,
    val password: String,
)

/**
 * Результат успешной регистрации с токенами авторизации.
 */
data class RegistrationResult(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
)
