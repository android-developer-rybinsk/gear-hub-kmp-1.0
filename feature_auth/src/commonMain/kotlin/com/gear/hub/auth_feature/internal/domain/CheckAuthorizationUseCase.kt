package com.gear.hub.auth_feature.internal.domain

/**
 * Юзкейс проверки сохранённого статуса авторизации.
 */
class CheckAuthorizationUseCase(
    private val sessionRepository: AuthSessionRepository,
) {

    /**
     * Возвращает true, если пользователь уже авторизован и можно пропустить экран авторизации.
     */
    suspend operator fun invoke(): Boolean = sessionRepository.isAuthorized()
}
