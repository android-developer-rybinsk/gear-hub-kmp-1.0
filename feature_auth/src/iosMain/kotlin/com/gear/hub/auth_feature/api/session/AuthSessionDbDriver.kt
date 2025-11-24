package com.gear.hub.auth_feature.api.session

import com.gear.hub.auth_feature.internal.data.session.IosAuthSessionDbDriver
import com.gear.hub.data.config.DatabaseFactory

/**
 * Фабрика платформенного драйвера.
 */
actual fun createAuthSessionDbDriver(factory: DatabaseFactory): AuthSessionDbDriver =
    IosAuthSessionDbDriver(factory)
