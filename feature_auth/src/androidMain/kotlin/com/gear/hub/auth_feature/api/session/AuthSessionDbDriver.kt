package com.gear.hub.auth_feature.api.session

import com.gear.hub.auth_feature.internal.data.session.AndroidAuthSessionDbDriver
import com.gear.hub.data.config.DatabaseFactory

/**
 * Фабрика платформенного драйвера.
 */
actual fun createAuthSessionDbDriver(factory: DatabaseFactory): AuthSessionDbDriver =
    AndroidAuthSessionDbDriver(factory)
