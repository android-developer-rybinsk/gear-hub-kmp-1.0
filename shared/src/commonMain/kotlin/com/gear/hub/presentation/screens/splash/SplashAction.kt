package com.gear.hub.presentation.screens.splash

sealed class SplashAction {
    object OnStartTimeout : SplashAction()
    object OnEndTimeout : SplashAction()
}