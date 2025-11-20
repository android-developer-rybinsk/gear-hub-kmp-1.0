package com.gear.hub.presentation.screens.main

import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router

class MainViewModel(
    private val router: Router
) : BaseViewModel<MainState, MainAction>(MainState()) {

    override fun onAction(action: MainAction) {
        when (action) {
            MainAction.NavigateNext -> router.back()
        }
    }
}