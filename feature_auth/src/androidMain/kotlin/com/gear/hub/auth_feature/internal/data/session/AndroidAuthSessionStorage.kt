package com.gear.hub.auth_feature.internal.data.session

import android.content.Context

/**
 * Android-реализация хранилища статуса авторизации на базе SharedPreferences.
 */
class AndroidAuthSessionStorage(
    private val context: Context,
) : AuthSessionStorage {

    /**
     * SharedPreferences для хранения флага авторизации.
     */
    private val prefs by lazy(LazyThreadSafetyMode.NONE) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override suspend fun isAuthorized(): Boolean = prefs.getBoolean(KEY_AUTHORIZED, false)

    override suspend fun setAuthorized(value: Boolean) {
        prefs.edit().putBoolean(KEY_AUTHORIZED, value).apply()
    }

    private companion object {
        /**
         * Имя файла SharedPreferences.
         */
        const val PREFS_NAME = "auth_session"

        /**
         * Ключ флага авторизации.
         */
        const val KEY_AUTHORIZED = "is_authorized"
    }
}
