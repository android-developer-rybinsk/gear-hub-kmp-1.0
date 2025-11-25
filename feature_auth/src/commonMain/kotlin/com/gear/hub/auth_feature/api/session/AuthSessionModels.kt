package com.gear.hub.auth_feature.api.session

/**
 * Модель сохранённых токенов в локальной базе.
 */
data class AuthCredentialsRecord(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
)

/**
 * Модель сохранённых пользовательских данных в локальной базе.
 */
data class AuthUserRecord(
    val userId: String,
    val email: String?,
    val phone: String?,
    val name: String,
)
