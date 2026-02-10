package com.gear.hub.auth_feature.internal.data

import com.gear.hub.auth_feature.api.session.AuthCredentialsRecord
import com.gear.hub.auth_feature.internal.data.model.AuthRefreshRequestDto
import com.gear.hub.auth_feature.internal.data.session.AuthSessionStorage
import com.gear.hub.auth_service.api.AuthApi
import com.gear.hub.network.auth.AuthSessionManager
import com.gear.hub.network.model.ApiResponse

/**
 * Управляет обновлением токена через AuthApi и хранение сессии.
 */
internal class AuthSessionManagerImpl(
    private val api: AuthApi,
    private val storage: AuthSessionStorage,
) : AuthSessionManager {
    override suspend fun refreshAccessToken(): String? {
        val refreshToken = storage.getCredentials()?.refreshToken ?: return null
        return when (val response = api.refresh(AuthRefreshRequestDto(refreshToken))) {
            is ApiResponse.Success -> {
                storage.setCredentials(
                    AuthCredentialsRecord(
                        accessToken = response.data.accessToken,
                        refreshToken = response.data.refreshToken,
                        expiresIn = response.data.expiresIn,
                    ),
                )
                response.data.accessToken
            }
            else -> null
        }
    }

    override suspend fun clearSession() {
        storage.clear()
    }
}
