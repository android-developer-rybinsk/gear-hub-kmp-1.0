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
    val tokens: RegistrationTokens,
    val user: RegisteredUser,
)

/**
 * Токены и срок их жизни.
 */
data class RegistrationTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
)

/**
 * Минимальный профиль пользователя из ответа регистрации.
 */
data class RegisteredUser(
    val id: String,
    val email: String?,
    val phone: String?,
    val name: String,
)
