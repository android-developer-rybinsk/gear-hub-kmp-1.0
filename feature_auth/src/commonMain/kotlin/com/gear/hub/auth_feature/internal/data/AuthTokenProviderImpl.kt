package com.gear.hub.auth_feature.internal.data

import com.gear.hub.auth_feature.internal.data.session.AuthSessionStorage
import com.gear.hub.network.auth.AuthTokenProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

internal class AuthTokenProviderImpl(
    private val storage: AuthSessionStorage,
) : AuthTokenProvider {
    override fun accessToken(): String? = runBlocking(Dispatchers.IO) {
        storage.getCredentials()?.accessToken
    }
}
