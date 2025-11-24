package com.gear.hub.auth_feature.internal.data.session

/**
 * SQL-запросы для таблицы статуса авторизации. Хранятся отдельно, чтобы
 * использовать один и тот же текст на Android и iOS.
 */
internal object AuthSessionQueries {
    /**
     * Создание таблицы и единственной строки состояния авторизации.
     */
    const val CREATE_TABLE = """
        CREATE TABLE IF NOT EXISTS auth_session (
            id INTEGER PRIMARY KEY CHECK (id = 1),
            authorized INTEGER NOT NULL DEFAULT 0
        );
    """

    /**
     * Создание таблицы для токенов авторизации.
     */
    const val CREATE_TABLE_CREDENTIALS = """
        CREATE TABLE IF NOT EXISTS auth_credentials (
            id INTEGER PRIMARY KEY CHECK (id = 1),
            access_token TEXT NOT NULL,
            refresh_token TEXT NOT NULL,
            expires_in INTEGER NOT NULL
        );
    """

    /**
     * Создание таблицы с данными пользователя.
     */
    const val CREATE_TABLE_USER = """
        CREATE TABLE IF NOT EXISTS auth_user (
            id INTEGER PRIMARY KEY CHECK (id = 1),
            user_id TEXT NOT NULL,
            email TEXT,
            phone TEXT,
            name TEXT NOT NULL
        );
    """

    /**
     * Вставка дефолтной строки, если её ещё нет.
     */
    const val INSERT_DEFAULT = "INSERT OR IGNORE INTO auth_session(id, authorized) VALUES(1, 0)"

    /**
     * Вставка или обновление токенов.
     */
    const val UPSERT_CREDENTIALS =
        "INSERT OR REPLACE INTO auth_credentials(id, access_token, refresh_token, expires_in) VALUES(1, :accessToken, :refreshToken, :expiresIn)"

    /**
     * Вставка или обновление данных пользователя.
     */
    const val UPSERT_USER =
        "INSERT OR REPLACE INTO auth_user(id, user_id, email, phone, name) VALUES(1, :userId, :email, :phone, :name)"

    /**
     * Чтение флага авторизации.
     */
    const val SELECT_AUTHORIZED = "SELECT authorized FROM auth_session WHERE id = 1"

    /**
     * Чтение сохранённых токенов.
     */
    const val SELECT_CREDENTIALS = "SELECT access_token, refresh_token, expires_in FROM auth_credentials WHERE id = 1"

    /**
     * Чтение сохранённых данных пользователя.
     */
    const val SELECT_USER = "SELECT user_id, email, phone, name FROM auth_user WHERE id = 1"

    /**
     * Обновление флага авторизации.
     */
    const val UPDATE_AUTHORIZED = "UPDATE auth_session SET authorized = :value WHERE id = 1"
}
