package com.gear.hub.auth_feature.internal.data.session

import com.gear.hub.auth_feature.api.session.AuthCredentialsRecord
import com.gear.hub.auth_feature.api.session.AuthUserRecord

/**
 * Хранилище статуса авторизации на уровне платформы.
 * Реализация находится в платформенных модулях и отвечает за персистентность флага авторизации.
 */
interface AuthSessionStorage {
    /**
     * Проверяет сохранённые токены, чтобы определить авторизацию пользователя.
     */
    suspend fun isAuthorized(): Boolean

    /**
     * Возвращает сохранённые токены, если они есть.
     */
    suspend fun getCredentials(): AuthCredentialsRecord?

    /**
     * Сохраняет токены авторизации в зашифрованной БД.
     */
    suspend fun setCredentials(credentials: AuthCredentialsRecord)

    /**
     * Сохраняет данные пользователя из ответа регистрации.
     */
    suspend fun setUser(user: AuthUserRecord)

    /**
     * Полностью очищает таблицы сессии и пользователя.
     */
    suspend fun clear()
}
