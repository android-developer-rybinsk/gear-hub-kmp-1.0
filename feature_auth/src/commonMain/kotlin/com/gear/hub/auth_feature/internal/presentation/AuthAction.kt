package com.gear.hub.auth_feature.internal.presentation

/**
 * События экрана авторизации в терминах MVI.
 */
sealed class AuthAction {
    data class UpdateName(val value: String) : AuthAction()
    data class UpdateLogin(val value: String) : AuthAction()
    data class UpdatePassword(val value: String) : AuthAction()
    data class UpdateConfirmPassword(val value: String) : AuthAction()
    data object StartRegistration : AuthAction()
    data object ProceedStep : AuthAction()
    data object SubmitRegistration : AuthAction()
    data object SubmitLogin : AuthAction()
    data object BackToStepOne : AuthAction()
}
