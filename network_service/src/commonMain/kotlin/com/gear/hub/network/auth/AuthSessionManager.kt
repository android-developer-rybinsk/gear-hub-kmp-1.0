package com.gear.hub.network.auth

/**
 * Управляет обновлением токена доступа и очисткой сессии при ошибках авторизации.
 */
interface AuthSessionManager {
    /**
     * Пытается обновить access token и возвращает новый токен, если удалось.
     */
    suspend fun refreshAccessToken(): String?

    /**
     * Очищает данные сессии при невозможности обновить токен.
     */
    suspend fun clearSession()
}

/**
 * Заглушка, когда управление сессией недоступно.
 */
object EmptyAuthSessionManager : AuthSessionManager {
    override suspend fun refreshAccessToken(): String? = null
    override suspend fun clearSession() = Unit
}
