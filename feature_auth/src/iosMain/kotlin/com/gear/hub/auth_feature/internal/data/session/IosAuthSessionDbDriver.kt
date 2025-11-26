package com.gear.hub.auth_feature.internal.data.session

import com.gear.hub.auth_feature.api.session.AuthCredentialsRecord
import com.gear.hub.auth_feature.api.session.AuthUserRecord
import com.gear.hub.auth_feature.api.session.AuthSessionDbDriver
import com.gear.hub.data.config.DatabaseFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSNumber
import platform.Foundation.NSUserDefaults

/**
 * iOS-драйвер для таблицы авторизации. Использует NSUserDefaults
 * для хранения сессии, чтобы исключить зависимость от SQLite на iOS.
 */
internal class IosAuthSessionDbDriver(
    factory: DatabaseFactory,
) : AuthSessionDbDriver {

    private val defaults = NSUserDefaults.standardUserDefaults

    override suspend fun ensureInitialized() {
        // NSUserDefaults не требует сложной инициализации, но выносим в фон
        // для согласованности с Android-драйвером.
        withContext(Dispatchers.Default) { defaults }
    }

    override fun setCredentials(credentials: AuthCredentialsRecord) {
        defaults.setObject(credentials.accessToken, ACCESS_TOKEN_KEY)
        defaults.setObject(credentials.refreshToken, REFRESH_TOKEN_KEY)
        defaults.setObject(NSNumber.numberWithLongLong(credentials.expiresIn), EXPIRES_IN_KEY)
    }

    override fun getCredentials(): AuthCredentialsRecord? {
        val accessToken = defaults.stringForKey(ACCESS_TOKEN_KEY)
        val refreshToken = defaults.stringForKey(REFRESH_TOKEN_KEY)
        val expiresIn = defaults.objectForKey(EXPIRES_IN_KEY) as? NSNumber
        return if (accessToken != null && refreshToken != null && expiresIn != null) {
            AuthCredentialsRecord(accessToken, refreshToken, expiresIn.longLongValue)
        } else {
            null
        }
    }

    override fun deleteCredentials() {
        defaults.removeObjectForKey(ACCESS_TOKEN_KEY)
        defaults.removeObjectForKey(REFRESH_TOKEN_KEY)
        defaults.removeObjectForKey(EXPIRES_IN_KEY)
    }

    override fun setUser(user: AuthUserRecord) {
        defaults.setObject(user.userId, USER_ID_KEY)
        defaults.setObject(user.name, USER_NAME_KEY)
        user.email?.let { defaults.setObject(it, USER_EMAIL_KEY) }
            ?: defaults.removeObjectForKey(USER_EMAIL_KEY)
        user.phone?.let { defaults.setObject(it, USER_PHONE_KEY) }
            ?: defaults.removeObjectForKey(USER_PHONE_KEY)
    }

    override fun deleteUser() {
        defaults.removeObjectForKey(USER_ID_KEY)
        defaults.removeObjectForKey(USER_NAME_KEY)
        defaults.removeObjectForKey(USER_EMAIL_KEY)
        defaults.removeObjectForKey(USER_PHONE_KEY)
    }

    private companion object {
        const val ACCESS_TOKEN_KEY = "auth.accessToken"
        const val REFRESH_TOKEN_KEY = "auth.refreshToken"
        const val EXPIRES_IN_KEY = "auth.expiresIn"
        const val USER_ID_KEY = "auth.user.id"
        const val USER_NAME_KEY = "auth.user.name"
        const val USER_EMAIL_KEY = "auth.user.email"
        const val USER_PHONE_KEY = "auth.user.phone"
    }
}
