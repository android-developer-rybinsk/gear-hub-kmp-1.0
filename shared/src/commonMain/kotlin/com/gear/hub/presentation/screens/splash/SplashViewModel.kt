package com.gear.hub.presentation.screens.splash

import androidx.lifecycle.viewModelScope
import com.gear.hub.navigation.DestinationApp
import com.gear.hub.auth_feature.internal.domain.CheckAuthorizationUseCase
import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel экрана Splash: показывает анимацию загрузки и решает, показывать ли авторизацию.
 */
class SplashViewModel(
    private val router: Router,
    private val checkAuthorizationUseCase: CheckAuthorizationUseCase,
) : BaseViewModel<SplashState, SplashAction>(SplashState()) {

    override fun onAction(action: SplashAction) {
        when (action) {
            SplashAction.OnStartTimeout -> startTimeout()
            SplashAction.OnEndTimeout -> navigateNext()
        }
    }

    /**
     * Запускает отсчёт сплеша, параллельно проверяя сохранённую авторизацию пользователя.
     */
    private fun startTimeout() {
        viewModelScope.launch {
            val authorized = checkAuthorizationUseCase()
            setState { it.copy(isAuthorized = authorized) }
            delay(2500)
            setState { it.copy(isTimeout = true) }
        }
    }

    /**
     * После окончания сплеша выбирает следующий экран: Main, если пользователь авторизован, иначе Auth.
     */
    private fun navigateNext() {
        val destination = if (currentState.isAuthorized) {
            DestinationApp.MainScreen
        } else {
            DestinationApp.AuthScreen
        }
        router.replaceAll(destination)
    }
}