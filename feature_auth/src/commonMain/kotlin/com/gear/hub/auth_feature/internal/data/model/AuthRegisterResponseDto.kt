package com.gear.hub.auth_feature.internal.data.model

import com.gear.hub.auth_feature.internal.domain.model.RegistrationResult
import com.gear.hub.auth_feature.internal.domain.model.RegistrationTokens
import com.gear.hub.auth_feature.internal.domain.model.RegisteredUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * DTO ответа регистрации, приходящего с бэкенда.
 */
@Serializable
data class AuthRegisterResponseDto(
    @SerialName("access_token")
    @JsonNames("accessToken")
    val accessToken: String,
    @SerialName("refresh_token")
    @JsonNames("refreshToken")
    val refreshToken: String,
    @SerialName("expires_in")
    @JsonNames("expiresIn")
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
    @SerialName("name")
    @JsonNames("full_name", "fullName")
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
