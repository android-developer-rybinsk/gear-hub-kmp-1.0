package com.gear.hub.auth_feature.internal.presentation

/**
 * Текущее состояние экрана авторизации.
 */
data class AuthState(
    val step: AuthStep = AuthStep.Step1(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val highlightError: Boolean = false,
)

/**
 * Шаги ввода данных на экране.
 */
sealed class AuthStep {
    data class Step1(val name: String = "", val login: String = "") : AuthStep()
    data class Step2(
        val name: String,
        val login: String,
        val password: String = "",
        val confirmPassword: String = "",
    ) : AuthStep()
}
