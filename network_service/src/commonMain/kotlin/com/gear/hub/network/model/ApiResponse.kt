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
    data class HttpError(val code: Int, val message: String? = null) : ApiResponse<Nothing>(), ApiError

    /**
     * Ошибка сети (нет подключения, таймаут и т.п.).
     */
    object NetworkError : ApiResponse<Nothing>(), ApiError

    /**
     * Непредвиденная ошибка, оборачивает оригинальное исключение.
     */
    data class UnknownError(val throwable: Throwable?) : ApiResponse<Nothing>(), ApiError

    /**
     * Общий интерфейс ошибок, позволяющий работать с ошибками единообразно в onError/fold.
     */
    sealed interface ApiError
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
inline fun <T> ApiResponse<T>.onError(block: (ApiResponse.ApiError) -> Unit): ApiResponse<T> {
    if (this is ApiResponse.ApiError) block(this)
    return this
}

/**
 * Сворачивает ответ в одно значение, не заставляя вызывать when с Success/Error вручную.
 */
inline fun <T, R> ApiResponse<T>.fold(
    onError: (ApiResponse.ApiError) -> R,
    onSuccess: (T) -> R,
): R = when (this) {
    is ApiResponse.Success -> onSuccess(data)
    is ApiResponse.ApiError -> onError(this)
}

/**
 * Преобразует успешный результат, оставляя тип ошибки неизменным.
 */
inline fun <T, R> ApiResponse<T>.map(transform: (T) -> R): ApiResponse<R> = when (this) {
    is ApiResponse.Success -> ApiResponse.Success(transform(data))
    is ApiResponse.HttpError -> this
    ApiResponse.NetworkError -> ApiResponse.NetworkError
    is ApiResponse.UnknownError -> this
}

/**
 * Преобразует успешный результат в другой ApiResponse, ошибки передаются как есть.
 */
inline fun <T, R> ApiResponse<T>.flatMap(transform: (T) -> ApiResponse<R>): ApiResponse<R> = when (this) {
    is ApiResponse.Success -> transform(data)
    is ApiResponse.HttpError -> this
    ApiResponse.NetworkError -> ApiResponse.NetworkError
    is ApiResponse.UnknownError -> this
}
