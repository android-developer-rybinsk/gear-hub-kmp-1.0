package com.gear.hub.auth_feature.api.session

import com.gear.hub.data.config.DatabaseFactory

/**
 * Общий контракт доступа к таблице авторизации. Платформенные реализации
 * должны использовать базовую инфраструктуру data_service (фабрику БД)
 * и выполнять запросы из [AuthSessionQueries].
 */
interface AuthSessionDbDriver {
    /**
     * Создаёт таблицу и дефолтную запись, если она ещё не создана.
     */
    suspend fun ensureInitialized()

    /**
     * Сохраняет пару access/refresh токенов и срок их жизни.
     */
    fun setCredentials(credentials: AuthCredentialsRecord)

    /**
     * Возвращает сохранённые токены, если они есть.
     */
    fun getCredentials(): AuthCredentialsRecord?

    /**
     * Удаляет сохранённые токены.
     */
    fun deleteCredentials()

    /**
     * Сохраняет данные пользователя из успешного ответа регистрации.
     */
    fun setUser(user: AuthUserRecord)

    /**
     * Удаляет сохранённые данные пользователя.
     */
    fun deleteUser()
}

/**
 * Фабричная функция для создания драйвера на платформе.
 */
expect fun createAuthSessionDbDriver(factory: DatabaseFactory): AuthSessionDbDriver
