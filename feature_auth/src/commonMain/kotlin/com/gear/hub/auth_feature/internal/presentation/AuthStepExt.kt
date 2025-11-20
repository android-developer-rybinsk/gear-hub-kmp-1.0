package com.gear.hub.auth_feature.internal.presentation

/**
 * Обновляет имя пользователя в зависимости от текущего шага авторизации.
 */
fun AuthStep.updateName(value: String): AuthStep = when (this) {
    is AuthStep.Step1 -> copy(name = value)
    is AuthStep.Step2 -> copy(name = value)
}

/**
 * Обновляет логин (почта/телефон) на активном шаге.
 */
fun AuthStep.updateLogin(value: String): AuthStep = when (this) {
    is AuthStep.Step1 -> copy(login = value)
    is AuthStep.Step2 -> copy(login = value)
}

/**
 * Обновляет пароль только на втором шаге авторизации.
 */
fun AuthStep.updatePassword(value: String): AuthStep = when (this) {
    is AuthStep.Step1 -> this
    is AuthStep.Step2 -> copy(password = value)
}

/**
 * Обновляет подтверждение пароля только на втором шаге.
 */
fun AuthStep.updateConfirmPassword(value: String): AuthStep = when (this) {
    is AuthStep.Step1 -> this
    is AuthStep.Step2 -> copy(confirmPassword = value)
}
