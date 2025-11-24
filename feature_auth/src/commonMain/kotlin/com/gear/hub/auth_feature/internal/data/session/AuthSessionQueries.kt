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
     * Вставка дефолтной строки, если её ещё нет.
     */
    const val INSERT_DEFAULT = "INSERT OR IGNORE INTO auth_session(id, authorized) VALUES(1, 0)"

    /**
     * Чтение флага авторизации.
     */
    const val SELECT_AUTHORIZED = "SELECT authorized FROM auth_session WHERE id = 1"

    /**
     * Обновление флага авторизации.
     */
    const val UPDATE_AUTHORIZED = "UPDATE auth_session SET authorized = :value WHERE id = 1"
}
