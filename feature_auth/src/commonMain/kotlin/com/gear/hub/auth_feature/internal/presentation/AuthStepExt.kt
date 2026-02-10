package com.gear.hub.auth_feature.internal.presentation

/**
 * Обновляет имя пользователя в зависимости от текущего шага авторизации.
 */
fun AuthStep.updateName(value: String): AuthStep = when (this) {
    is AuthStep.RegisterStep1 -> copy(name = value)
    is AuthStep.RegisterStep2 -> copy(name = value)
    is AuthStep.Login -> this
}

/**
 * Обновляет логин (почта/телефон) на активном шаге.
 */
fun AuthStep.updateLogin(value: String): AuthStep = when (this) {
    is AuthStep.RegisterStep1 -> copy(login = value)
    is AuthStep.RegisterStep2 -> copy(login = value)
    is AuthStep.Login -> copy(login = value)
}

/**
 * Обновляет пароль только на втором шаге авторизации.
 */
fun AuthStep.updatePassword(value: String): AuthStep = when (this) {
    is AuthStep.RegisterStep1 -> this
    is AuthStep.RegisterStep2 -> copy(password = value)
    is AuthStep.Login -> copy(password = value)
}

/**
 * Обновляет подтверждение пароля только на втором шаге.
 */
fun AuthStep.updateConfirmPassword(value: String): AuthStep = when (this) {
    is AuthStep.RegisterStep1 -> this
    is AuthStep.RegisterStep2 -> copy(confirmPassword = value)
    is AuthStep.Login -> this
}
