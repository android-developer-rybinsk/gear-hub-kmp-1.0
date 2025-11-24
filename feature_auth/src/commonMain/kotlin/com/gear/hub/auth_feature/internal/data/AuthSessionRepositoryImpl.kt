package com.gear.hub.auth_feature.internal.data

import com.gear.hub.auth_feature.internal.data.session.AuthSessionStorage
import com.gear.hub.auth_feature.internal.domain.AuthSessionRepository

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
}
