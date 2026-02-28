package com.gear.hub.network.auth

/**
 * Провайдер токена для авторизованных запросов.
 */
interface AuthTokenProvider {
    fun accessToken(): String?
}

