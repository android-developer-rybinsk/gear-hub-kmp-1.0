package com.gear.hub.auth_feature.internal.data.session

/**
 * SQL-запросы для таблиц сессии авторизации. Хранятся отдельно, чтобы
 * использовать один и тот же текст на Android и iOS.
 */
internal object AuthSessionQueries {
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
     * Чтение сохранённых токенов.
     */
    const val SELECT_CREDENTIALS = "SELECT id, access_token, refresh_token, expires_in FROM auth_credentials WHERE id = 1"

    /**
     * Чтение сохранённых данных пользователя.
     */
    const val SELECT_USER = "SELECT id, user_id, email, phone, name FROM auth_user WHERE id = 1"

    /**
     * Удаление всех сохранённых токенов.
     */
    const val DELETE_CREDENTIALS = "DELETE FROM auth_credentials"

    /**
     * Удаление сохранённых данных пользователя.
     */
    const val DELETE_USER = "DELETE FROM auth_user"
}
