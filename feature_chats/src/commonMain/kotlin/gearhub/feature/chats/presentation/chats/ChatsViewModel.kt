package gearhub.feature.chats.presentation.chats

import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router

class ChatsViewModel(
    private val router: Router
) : BaseViewModel<ChatsState, ChatsAction>(ChatsState()) {

    override fun onAction(action: ChatsAction) {
        when (action) {
            ChatsAction.Back -> {}
        }
    }
}