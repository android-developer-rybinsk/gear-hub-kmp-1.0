package com.gear.hub.auth_feature.internal.data

import com.gear.hub.auth_feature.api.session.AuthSessionDbDriver
import com.gear.hub.network.auth.AuthTokenProvider

internal class AuthTokenProviderImpl(
    private val dbDriver: AuthSessionDbDriver,
) : AuthTokenProvider {
    override fun accessToken(): String? = dbDriver.getCredentials()?.accessToken
}
