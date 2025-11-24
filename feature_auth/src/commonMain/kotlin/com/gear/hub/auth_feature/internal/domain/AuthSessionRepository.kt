package com.gear.hub.auth_feature.internal.domain

/**
 * Репозиторий управления сессией авторизации.
 * Позволяет определить, нужно ли показывать экран авторизации, и сохранить успешный вход.
 */
interface AuthSessionRepository {
    /**
     * Проверяет сохранённый признак авторизации пользователя.
     */
    suspend fun isAuthorized(): Boolean

    /**
     * Сохраняет статус авторизации после успешной регистрации или логина.
     */
    suspend fun setAuthorized(value: Boolean)
}
