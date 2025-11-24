package com.gear.hub.presentation.screens.splash

/**
 * Состояние экрана Splash: хранит, завершился ли таймер, и признак авторизации пользователя.
 */
data class SplashState(
    /**
     * Таймер завершён и можно выполнять навигацию дальше.
     */
    val isTimeout: Boolean = false,

    /**
     * Пользователь уже авторизован и может миновать экран авторизации.
     */
    val isAuthorized: Boolean = false,
)