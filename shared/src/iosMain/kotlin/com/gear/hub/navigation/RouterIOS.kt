package com.gear.hub.navigation

import gear.hub.core.di.IOSFlow
import gear.hub.core.navigation.Destination
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import gear.hub.core.navigation.Router
import gear.hub.core.navigation.NavigationAction

class RouterIOS : Router {
    private val _actions = MutableSharedFlow<NavigationAction>(extraBufferCapacity = 1)
    val actions: SharedFlow<NavigationAction> = _actions

    override fun navigate(destination: Destination) {
        _actions.tryEmit(NavigationAction.Navigate(destination))
    }

    override fun back() {
        _actions.tryEmit(NavigationAction.Back)
    }

    override fun popUpTo(destination: Destination, inclusive: Boolean) {
        _actions.tryEmit(NavigationAction.PopUpTo(destination, inclusive))
    }

    override fun replaceAll(destination: Destination) {
        _actions.tryEmit(NavigationAction.ReplaceAll(destination))
    }

    /** мост для action между Swift и Kotlin */
    fun iosActions(): IOSFlow<NavigationAction> = IOSFlow(actions)
}