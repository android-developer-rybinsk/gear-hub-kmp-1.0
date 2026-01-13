package com.gear.hub.network.util

/**
 * Обеспечивает наличие завершающего слэша у baseUrl, чтобы Retrofit/HttpClient корректно собирали пути.
 */
fun String.ensureTrailingSlash(): String = if (endsWith('/')) this else "$this/"
