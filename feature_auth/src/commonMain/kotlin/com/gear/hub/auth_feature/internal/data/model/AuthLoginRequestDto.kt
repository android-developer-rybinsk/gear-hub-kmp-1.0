package com.gear.hub.auth_feature.internal.data.model

import com.gear.hub.auth_feature.internal.domain.model.LoginPayload
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO запроса логина.
 */
@Serializable
data class AuthLoginRequestDto(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
)

/**
 * Маппинг доменной модели логина в DTO.
 */
internal fun LoginPayload.toDto(): AuthLoginRequestDto = AuthLoginRequestDto(
    email = email,
    password = password,
)
