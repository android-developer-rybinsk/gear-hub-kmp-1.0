package com.gear.hub.auth_feature.internal.presentation

/**
 * Текущее состояние экрана авторизации.
 */
data class AuthState(
    val step: AuthStep = AuthStep.Login(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val highlightError: Boolean = false,
)

/**
 * Шаги ввода данных на экране.
 */
sealed class AuthStep {
    data class Login(val login: String = "", val password: String = "") : AuthStep()
    data class RegisterStep1(val name: String = "", val login: String = "") : AuthStep()
    data class RegisterStep2(
        val name: String,
        val login: String,
        val password: String = "",
        val confirmPassword: String = "",
    ) : AuthStep()
}
