package com.gear.hub.auth_feature.internal.domain

/**
 * Юзкейс выхода пользователя из системы.
 */
class LogoutUseCase(
    private val sessionRepository: AuthSessionRepository,
) {

    /**
     * Сбрасывает признак авторизации и очищает связанные данные сессии, если хранилище поддерживает это.
     */
    suspend operator fun invoke() {
        sessionRepository.clearSession()
    }
}
