package com.gear.hub.auth_feature.internal.data.session

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Общая реализация хранилища статуса авторизации, работающая через
 * платформенный драйвер и единый набор SQL-запросов.
 */
internal class AuthSessionStorageImpl(
    private val driver: AuthSessionDbDriver,
) : AuthSessionStorage {

    override suspend fun isAuthorized(): Boolean = withContext(Dispatchers.IO) {
        driver.ensureInitialized()
        driver.getAuthorized()
    }

    override suspend fun setAuthorized(value: Boolean) {
        withContext(Dispatchers.IO) {
            driver.ensureInitialized()
            driver.setAuthorized(value)
        }
    }

    override suspend fun setCredentials(credentials: AuthCredentialsRecord) {
        withContext(Dispatchers.IO) {
            driver.ensureInitialized()
            driver.setCredentials(credentials)
        }
    }

    override suspend fun setUser(user: AuthUserRecord) {
        withContext(Dispatchers.IO) {
            driver.ensureInitialized()
            driver.setUser(user)
        }
    }
}
