package com.gear.hub.auth_feature.internal.data.session

import platform.Foundation.NSUserDefaults

/**
 * iOS-реализация хранилища статуса авторизации через NSUserDefaults.
 */
class IosAuthSessionStorage : AuthSessionStorage {

    /**
     * Стандартное пользовательское хранилище iOS.
     */
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults

    override suspend fun isAuthorized(): Boolean = defaults.boolForKey(KEY_AUTHORIZED)

    override suspend fun setAuthorized(value: Boolean) {
        defaults.setBool(value, forKey = KEY_AUTHORIZED)
    }

    private companion object {
        /**
         * Ключ флага авторизации для NSUserDefaults.
         */
        const val KEY_AUTHORIZED = "is_authorized"
    }
}
