package com.gear.hub.auth_feature.internal.domain

import com.gear.hub.auth_feature.internal.domain.model.LoginPayload
import com.gear.hub.auth_feature.internal.domain.model.RegistrationResult
import com.gear.hub.network.model.ApiResponse

/**
 * Юзкейс логина пользователя с базовой валидацией.
 */
class LoginUserUseCase(
    private val repository: AuthRepository,
    private val sessionRepository: AuthSessionRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): ApiResponse<RegistrationResult> {
        if (!isEmail(email)) {
            return ApiResponse.HttpError(400, "Неверный формат почты")
        }

        val payload = LoginPayload(
            email = email.trim(),
            password = password,
        )

        return repository.login(payload).also { response ->
            if (response is ApiResponse.Success) {
                sessionRepository.persistSession(
                    tokens = response.data.tokens,
                    user = response.data.user,
                )
            }
        }
    }

    /**
     * Проверяет базовый формат email (минимально — наличие '@' и домена).
     */
    private fun isEmail(value: String): Boolean =
        value.contains('@') && value.contains('.')
}
