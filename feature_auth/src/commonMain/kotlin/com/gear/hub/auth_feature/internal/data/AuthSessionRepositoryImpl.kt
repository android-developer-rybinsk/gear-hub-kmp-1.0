package com.gear.hub.auth_feature.internal.data

import com.gear.hub.auth_feature.api.session.AuthCredentialsRecord
import com.gear.hub.auth_feature.api.session.AuthUserRecord
import com.gear.hub.auth_feature.internal.data.session.AuthSessionStorage
import com.gear.hub.auth_feature.internal.domain.AuthSessionRepository
import com.gear.hub.auth_feature.internal.domain.model.RegisteredUser
import com.gear.hub.auth_feature.internal.domain.model.RegistrationTokens

/**
 * Реализация репозитория сессии авторизации через платформенное хранилище.
 */
class AuthSessionRepositoryImpl(
    private val storage: AuthSessionStorage,
) : AuthSessionRepository {

    override suspend fun isAuthorized(): Boolean = storage.isAuthorized()

    override suspend fun setAuthorized(value: Boolean) {
        storage.setAuthorized(value)
    }

    override suspend fun persistSession(tokens: RegistrationTokens, user: RegisteredUser) {
        storage.setCredentials(
            AuthCredentialsRecord(
                accessToken = tokens.accessToken,
                refreshToken = tokens.refreshToken,
                expiresIn = tokens.expiresIn,
            ),
        )
        storage.setUser(
            AuthUserRecord(
                userId = user.id,
                email = user.email,
                phone = user.phone,
                name = user.name,
            ),
        )
    }
}
