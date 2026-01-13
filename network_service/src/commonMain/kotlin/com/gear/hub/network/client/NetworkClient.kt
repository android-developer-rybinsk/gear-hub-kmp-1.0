package com.gear.hub.network.client

/**
 * Общий тип сетевого клиента для KMP.
 *
 * На Android фактически соответствует [retrofit2.Retrofit],
 * на iOS — [io.ktor.client.HttpClient].
 */
expect class NetworkClient
