package com.gear.hub.auth_feature.internal.data.session

import com.gear.hub.auth_feature.api.session.AuthCredentialsRecord
import com.gear.hub.auth_feature.api.session.AuthUserRecord

/**
 * Хранилище статуса авторизации на уровне платформы.
 * Реализация находится в платформенных модулях и отвечает за персистентность флага авторизации.
 */
internal interface AuthSessionStorage {
    /**
     * Возвращает признак того, что пользователь уже авторизован (флаг записан после успешного входа/регистрации).
     */
    suspend fun isAuthorized(): Boolean

    /**
     * Сохраняет флаг авторизации, чтобы при следующем запуске можно было пропустить экран входа.
     */
    suspend fun setAuthorized(value: Boolean)

    /**
     * Сохраняет токены авторизации в зашифрованной БД.
     */
    suspend fun setCredentials(credentials: AuthCredentialsRecord)

    /**
     * Сохраняет данные пользователя из ответа регистрации.
     */
    suspend fun setUser(user: AuthUserRecord)
}
