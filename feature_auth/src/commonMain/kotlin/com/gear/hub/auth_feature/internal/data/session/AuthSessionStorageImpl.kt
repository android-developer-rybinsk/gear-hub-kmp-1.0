package com.gear.hub.auth_feature.internal.data.session

import com.gear.hub.auth_feature.api.session.AuthCredentialsRecord
import com.gear.hub.auth_feature.api.session.AuthSessionDbDriver
import com.gear.hub.auth_feature.api.session.AuthUserRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Общая реализация хранилища статуса авторизации, работающая через
 * платформенный драйвер и единый набор SQL-запросов.
 */
class AuthSessionStorageImpl(
    private val driver: AuthSessionDbDriver,
) : AuthSessionStorage {
    private var cachedCredentials: AuthCredentialsRecord? = null
    private var cachedUser: AuthUserRecord? = null

    override suspend fun isAuthorized(): Boolean = withContext(Dispatchers.IO) {
        driver.ensureInitialized()
        cachedCredentials ?: driver.getCredentials()?.also { cachedCredentials = it } != null
    }

    override suspend fun getCredentials(): AuthCredentialsRecord? = withContext(Dispatchers.IO) {
        driver.ensureInitialized()
        cachedCredentials ?: driver.getCredentials()?.also { cachedCredentials = it }
    }

    override suspend fun setCredentials(credentials: AuthCredentialsRecord) {
        withContext(Dispatchers.IO) {
            driver.ensureInitialized()
            cachedCredentials = credentials
            driver.setCredentials(credentials)
        }
    }

    override suspend fun setUser(user: AuthUserRecord) {
        withContext(Dispatchers.IO) {
            driver.ensureInitialized()
            cachedUser = user
            driver.setUser(user)
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            driver.ensureInitialized()
            cachedCredentials = null
            cachedUser = null
            driver.deleteCredentials()
            driver.deleteUser()
        }
    }
}
