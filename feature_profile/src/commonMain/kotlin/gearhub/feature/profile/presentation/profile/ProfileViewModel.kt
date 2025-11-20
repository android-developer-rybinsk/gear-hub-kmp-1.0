package gearhub.feature.profile.presentation.profile

import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router

class ProfileViewModel(
    private val router: Router
) : BaseViewModel<ProfileState, ProfileAction>(ProfileState()) {

    override fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.Back -> {}
        }
    }
}