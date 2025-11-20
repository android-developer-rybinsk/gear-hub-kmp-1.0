package com.gear.hub.auth_feature.api

import gear.hub.core.navigation.Destination

/**
 * Конфигурация навигации модуля авторизации: куда идти после успешной регистрации
 * (в нашем случае — на MainScreen, который по умолчанию открывает MenuScreen).
 */
data class AuthNavigationConfig(
    val successDestination: Destination
)
