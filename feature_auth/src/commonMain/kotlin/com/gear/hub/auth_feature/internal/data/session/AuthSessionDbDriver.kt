package com.gear.hub.auth_feature.internal.data.session

import com.gear.hub.data.config.DatabaseRuntime

/**
 * Общий контракт доступа к таблице авторизации. Платформенные реализации
 * должны использовать базовую инфраструктуру data_service (runtime) и
 * выполнять запросы из [AuthSessionQueries].
 */
internal interface AuthSessionDbDriver {
    /**
     * Создаёт таблицу и дефолтную запись, если она ещё не создана.
     */
    fun ensureInitialized()

    /**
     * Получает признак авторизации из таблицы.
     */
    fun getAuthorized(): Boolean

    /**
     * Устанавливает признак авторизации.
     */
    fun setAuthorized(value: Boolean)
}

/**
 * Фабричная функция для создания драйвера на платформе.
 */
internal expect fun createAuthSessionDbDriver(runtime: DatabaseRuntime): AuthSessionDbDriver
