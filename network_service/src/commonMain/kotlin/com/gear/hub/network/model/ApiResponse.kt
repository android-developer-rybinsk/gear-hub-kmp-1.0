package com.gear.hub.network.model

/**
 * Базовая обертка сетевых ответов для унифицированной обработки ошибок.
 * Используется всеми фичами, чтобы возвращать один и тот же контракт из сервисов.
 */
sealed class ApiResponse<out T> {
    /**
     * Успешный ответ с телом нужного типа.
     */
    data class Success<T>(val data: T) : ApiResponse<T>()

    /**
     * Ответ с ошибкой HTTP. Код и сообщение приходят из сервера или мапятся из тела.
     */
    data class HttpError(val code: Int, val message: String? = null) : ApiResponse<Nothing>()

    /**
     * Ошибка сети (нет подключения, таймаут и т.п.).
     */
    object NetworkError : ApiResponse<Nothing>()

    /**
     * Непредвиденная ошибка, оборачивает оригинальное исключение.
     */
    data class UnknownError(val throwable: Throwable?) : ApiResponse<Nothing>()
}

/**
 * Удобный хелпер для обработки успешного результата без when.
 */
inline fun <T> ApiResponse<T>.onSuccess(block: (T) -> Unit): ApiResponse<T> {
    if (this is ApiResponse.Success) block(data)
    return this
}

/**
 * Удобный хелпер для обработки любой ошибки без when.
 */
inline fun <T> ApiResponse<T>.onError(block: (ApiResponse<Nothing>) -> Unit): ApiResponse<T> {
    if (this !is ApiResponse.Success) block(this)
    return this
}
