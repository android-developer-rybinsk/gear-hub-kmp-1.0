package com.gear.hub.auth_feature.internal.data.model

import com.gear.hub.auth_feature.internal.domain.model.RegistrationResult
import com.gear.hub.auth_feature.internal.domain.model.RegistrationTokens
import com.gear.hub.auth_feature.internal.domain.model.RegisteredUser
import kotlinx.serialization.Serializable

/**
 * DTO ответа регистрации, приходящего с бэкенда.
 */
@Serializable
data class AuthRegisterResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: AuthUserDto,
)

/**
 * DTO пользователя из ответа.
 */
@Serializable
data class AuthUserDto(
    val email: String?,
    val phone: String?,
    val name: String,
    val id: String,
)

/**
 * Маппинг сетевой модели в доменную сущность результата регистрации.
 */
internal fun AuthRegisterResponseDto.toDomain(): RegistrationResult = RegistrationResult(
    tokens = RegistrationTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresIn = expiresIn,
    ),
    user = RegisteredUser(
        id = user.id,
        email = user.email,
        phone = user.phone,
        name = user.name,
    ),
)
