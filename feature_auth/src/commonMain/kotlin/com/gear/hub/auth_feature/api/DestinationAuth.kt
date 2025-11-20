package com.gear.hub.auth_feature.api

import gear.hub.core.navigation.Destination

/**
 * Описание роутов экрана авторизации для навигации через общий Router.
 */
sealed class DestinationAuth(override val route: String) : Destination(route) {
    /** Экран двухшаговой регистрации/авторизации. */
    data object AuthScreen : DestinationAuth("auth")
}
