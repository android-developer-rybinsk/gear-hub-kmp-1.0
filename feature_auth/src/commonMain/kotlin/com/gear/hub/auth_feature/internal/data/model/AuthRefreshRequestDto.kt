package com.gear.hub.auth_feature.internal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO запроса обновления токена.
 */
@Serializable
data class AuthRefreshRequestDto(
    @SerialName("refreshToken")
    val refreshToken: String,
)
