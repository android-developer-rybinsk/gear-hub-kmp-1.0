package com.gear.hub.auth_feature.internal.data.model

import com.gear.hub.auth_feature.internal.domain.model.RegistrationResult
import kotlinx.serialization.Serializable

/**
 * DTO ответа регистрации, приходящего с бэкенда.
 */
@Serializable
data class AuthRegisterResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
)

/**
 * Маппинг сетевой модели в доменную сущность результата регистрации.
 */
internal fun AuthRegisterResponseDto.toDomain(): RegistrationResult = RegistrationResult(
    accessToken = accessToken,
    refreshToken = refreshToken,
    expiresIn = expiresIn,
)
