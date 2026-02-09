package com.gear.hub.network.auth

/**
 * Провайдер токена для авторизованных запросов.
 */
interface AuthTokenProvider {
    fun accessToken(): String?
}

/**
 * Заглушка, когда токен недоступен.
 */
object EmptyAuthTokenProvider : AuthTokenProvider {
    override fun accessToken(): String? = null
}
