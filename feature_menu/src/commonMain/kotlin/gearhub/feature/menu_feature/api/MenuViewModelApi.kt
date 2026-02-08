package gearhub.feature.menu_feature.api

import gear.hub.core.di.IOSStateFlow
import gearhub.feature.menu_feature.internal.presentation.menu.MenuAction
import gearhub.feature.menu_feature.internal.presentation.menu.MenuStateUI
import kotlinx.coroutines.flow.StateFlow

/**
 * Публичный контракт для MenuViewModel.
 */
interface MenuViewModelApi {
    val state: StateFlow<MenuStateUI>

    fun iosState(): IOSStateFlow<MenuStateUI>

    fun onAction(action: MenuAction)
}
