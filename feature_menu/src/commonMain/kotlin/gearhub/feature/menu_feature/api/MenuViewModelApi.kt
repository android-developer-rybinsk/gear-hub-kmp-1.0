package gearhub.feature.menu_feature.api

import gear.hub.core.di.IOSStateFlow
import gearhub.feature.menu_feature.internal.presentation.menu.MenuAction
import gearhub.feature.menu_feature.internal.presentation.menu.MenuState
import kotlinx.coroutines.flow.StateFlow

/**
 * Публичный контракт для MenuViewModel.
 */
interface MenuViewModelApi {
    val state: StateFlow<MenuState>

    fun iosState(): IOSStateFlow<MenuState>

    fun onAction(action: MenuAction)
}
