package com.gear.hub.auth_feature.internal.presentation

import androidx.lifecycle.viewModelScope
import com.gear.hub.auth_feature.api.AuthNavigationConfig
import com.gear.hub.auth_feature.internal.domain.RegisterUserUseCase
import com.gear.hub.network.model.ApiResponse
import com.gear.hub.network.model.onError
import com.gear.hub.network.model.onSuccess
import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import kotlinx.coroutines.launch

/**
 * ViewModel экрана авторизации. Управляет шагами ввода и отправкой запроса регистрации.
 */
class AuthViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    private val router: Router,
    private val navigationConfig: AuthNavigationConfig,
) : BaseViewModel<AuthState, AuthAction>(AuthState()) {

    override fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.UpdateName -> setState { state -> state.copy(step = state.step.updateName(action.value)) }
            is AuthAction.UpdateLogin -> setState { state -> state.copy(step = state.step.updateLogin(action.value)) }
            is AuthAction.UpdatePassword -> setState { state -> state.copy(step = state.step.updatePassword(action.value)) }
            is AuthAction.UpdateConfirmPassword -> setState { state -> state.copy(step = state.step.updateConfirmPassword(action.value)) }
            AuthAction.ProceedStep -> proceedToPassword()
            AuthAction.BackToStepOne -> backToLogin()
            AuthAction.Submit -> register()
        }
    }

    /**
     * Переходит к шагу ввода пароля, если имя и логин заполнены.
     */
    private fun proceedToPassword() {
        val step = currentState.step
        if (step is AuthStep.Step1 && step.name.isNotBlank() && step.login.isNotBlank()) {
            setState { it.copy(step = AuthStep.Step2(step.name.trim(), step.login.trim()), errorMessage = null, highlightError = false) }
        } else {
            setState { it.copy(errorMessage = "Введите имя и логин", highlightError = true) }
        }
    }

    /**
     * Возвращает пользователя на первый шаг, сохраняя введённые данные.
     */
    private fun backToLogin() {
        val step = currentState.step
        if (step is AuthStep.Step2) {
            setState {
                it.copy(
                    step = AuthStep.Step1(name = step.name, login = step.login),
                    errorMessage = null,
                    highlightError = false,
                )
            }
        }
    }

    /**
     * Выполняет регистрацию на втором шаге и обрабатывает сетевые ответы.
     */
    private fun register() {
        val step = currentState.step
        if (step !is AuthStep.Step2) return
        if (step.password.isBlank() || step.confirmPassword.isBlank()) {
            setState { it.copy(errorMessage = "Заполните оба поля пароля", highlightError = true) }
            return
        }
        if (step.password != step.confirmPassword) {
            setState { it.copy(errorMessage = "Пароли не совпадают", highlightError = true) }
            return
        }

        setState { it.copy(isLoading = true, errorMessage = null, highlightError = false) }

        viewModelScope.launch {
            registerUserUseCase(step.name, step.login, step.password)
                .onSuccess {
                    setState { it.copy(isLoading = false) }
                    router.replaceAll(navigationConfig.successDestination)
                }
                .onError { error ->
                    when (error) {
                        is ApiResponse.HttpError -> {
                            val message = if (error.code == 409) {
                                "Пользователь уже зарегистрирован"
                            } else {
                                error.message ?: "Ошибка регистрации"
                            }
                            setState { it.copy(isLoading = false, errorMessage = message, highlightError = true) }
                        }

                        ApiResponse.NetworkError -> {
                            setState {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Проблема с интернет соединением. Попробуйте позже",
                                    highlightError = true,
                                )
                            }
                        }

                        is ApiResponse.UnknownError -> {
                            setState { it.copy(isLoading = false, errorMessage = error.throwable?.message, highlightError = true) }
                        }
                    }
                }
        }
    }
}
