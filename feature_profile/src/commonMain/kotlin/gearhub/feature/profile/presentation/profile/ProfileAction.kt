package gearhub.feature.profile.presentation.profile

sealed class ProfileAction {
    data object Back : ProfileAction()
    data object Logout : ProfileAction()
}