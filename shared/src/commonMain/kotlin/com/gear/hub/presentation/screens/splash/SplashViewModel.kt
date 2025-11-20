package com.gear.hub.presentation.screens.splash

import androidx.lifecycle.viewModelScope
import com.gear.hub.navigation.DestinationApp
import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val router: Router
) : BaseViewModel<SplashState, SplashAction>(SplashState()) {

    override fun onAction(action: SplashAction) {
        when (action) {
            SplashAction.OnStartTimeout -> startTimeout()
            SplashAction.OnEndTimeout -> router.replaceAll(DestinationApp.MainScreen)
        }
    }

    private fun startTimeout() {
        viewModelScope.launch {
            delay(2500)
            setState { it.copy(isTimeout = true) }
        }
    }
}