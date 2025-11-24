package com.gear.hub.auth_feature.internal.domain

import com.gear.hub.auth_feature.internal.domain.model.RegistrationPayload
import com.gear.hub.auth_feature.internal.domain.model.RegistrationResult
import com.gear.hub.network.model.ApiResponse

/**
 * Юзкейс регистрации пользователя с базовой валидацией ввода.
 */
class RegisterUserUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        name: String,
        login: String,
        password: String,
    ): ApiResponse<RegistrationResult> {
        val email = login.takeIf { isEmail(it) }
        val phone = login.takeIf { isPhone(it) }

        if (email == null && phone == null) {
            return ApiResponse.HttpError(400, "Неверный формат почты или телефона")
        }

        val payload = RegistrationPayload(
            name = name.trim(),
            emailOrPhone = email ?: phone,
            password = password,
        )
        return repository.register(payload)
    }

    /**
     * Проверяет базовый формат email (минимально — наличие '@' и домена).
     */
    private fun isEmail(value: String): Boolean =
        value.contains('@') && value.contains('.')

    /**
     * Проверяет, что строка похожа на телефон РФ (+7/8), чтобы применить маску на UI.
     */
    private fun isPhone(value: String): Boolean =
        value.startsWith("+7") || value.startsWith("8")
}
