package com.gear.hub.auth_service.model

import kotlinx.serialization.Serializable

/**
 * Тело запроса регистрации пользователя.
 */
@Serializable
data class AuthRegisterRequest(
    val name: String,
    val email: String?,
    val password: String,
)
