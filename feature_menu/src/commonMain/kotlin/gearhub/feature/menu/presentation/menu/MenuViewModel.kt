package gearhub.feature.menu.presentation.menu

import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router

class MenuViewModel(
    private val router: Router
) : BaseViewModel<MenuState, MenuAction>(MenuState()) {

    override fun onAction(action: MenuAction) {
        when (action) {
            MenuAction.Back -> {}
        }
    }
}