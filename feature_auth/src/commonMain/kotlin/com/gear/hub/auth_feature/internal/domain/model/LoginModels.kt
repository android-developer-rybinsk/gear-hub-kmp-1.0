package com.gear.hub.auth_feature.internal.domain.model

/**
 * Данные для логина пользователя.
 */
data class LoginPayload(
    val email: String,
    val password: String,
)
