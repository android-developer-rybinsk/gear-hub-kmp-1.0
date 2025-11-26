package gearhub.feature.profile.api

import gear.hub.core.navigation.Destination

/**
 * Конфигурация навигации модуля профиля: куда отправлять пользователя после выхода.
 */
data class ProfileNavigationConfig(
    val logoutDestination: Destination,
)
