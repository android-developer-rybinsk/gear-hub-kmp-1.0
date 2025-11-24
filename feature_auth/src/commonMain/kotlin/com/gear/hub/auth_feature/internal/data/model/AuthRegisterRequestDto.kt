package com.gear.hub.auth_feature.internal.data.model

import com.gear.hub.auth_feature.internal.domain.model.RegistrationPayload
import kotlinx.serialization.Serializable

/**
 * DTO запроса регистрации пользователя для сетевого слоя.
 */
@Serializable
data class AuthRegisterRequestDto(
    val name: String,
    val email: String?,
    val password: String,
)

/**
 * Конвертация доменной модели регистрации в DTO для отправки на бэкенд.
 */
internal fun RegistrationPayload.toDto(): AuthRegisterRequestDto = AuthRegisterRequestDto(
    name = name,
    email = emailOrPhone,
    password = password,
)
