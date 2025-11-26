package gearhub.feature.profile.presentation.profile

import androidx.lifecycle.viewModelScope
import com.gear.hub.auth_feature.internal.domain.LogoutUseCase
import com.gear.hub.navigation.DestinationApp
import gear.hub.core.BaseViewModel
import gear.hub.core.navigation.Router
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val router: Router,
    private val logoutUseCase: LogoutUseCase,
) : BaseViewModel<ProfileState, ProfileAction>(ProfileState()) {

    override fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.Back -> {}
            ProfileAction.Logout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            router.replaceAll(DestinationApp.AuthScreen)
        }
    }
}