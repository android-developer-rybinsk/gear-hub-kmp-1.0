package com.gear.hub.auth_feature.internal.data.session

import com.gear.hub.auth_feature.api.session.AuthCredentialsRecord
import com.gear.hub.auth_feature.api.session.AuthUserRecord
import com.gear.hub.auth_feature.api.session.AuthSessionDbDriver
import com.gear.hub.data.config.DatabaseFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Драйвер доступа к таблице сессии на Android, опирающийся на Room и
 * шифрование SQLCipher, подготовленное в data_service. Все операции
 * выполняются через DAO, чтобы минимизировать ручной SQL в платформенном слое.
 */
internal class AndroidAuthSessionDbDriver(
    factory: DatabaseFactory,
) : AuthSessionDbDriver {

    /**
     * Единый Room-инстанс с SQLCipher, создаётся через базовый runtime.
     */
    private val database: AuthSessionDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        factory.roomDatabaseBuilder(AuthSessionDatabase::class.java)
            .build()
    }

    private val dao: AuthSessionDao by lazy(LazyThreadSafetyMode.NONE) { database.authSessionDao() }

    override suspend fun ensureInitialized() {
        withContext(Dispatchers.IO) {
            // Прогреваем базу и DAO, чтобы Room собрал синглтон и создал таблицы.
            dao
        }
    }

    override fun setCredentials(credentials: AuthCredentialsRecord) {
        dao.upsertCredentials(
            AuthCredentialsEntity(
                accessToken = credentials.accessToken,
                refreshToken = credentials.refreshToken,
                expiresIn = credentials.expiresIn,
            ),
        )
    }

    override fun getCredentials(): AuthCredentialsRecord? = dao.getCredentials()?.let {
        AuthCredentialsRecord(
            accessToken = it.accessToken,
            refreshToken = it.refreshToken,
            expiresIn = it.expiresIn,
        )
    }

    override fun deleteCredentials() {
        dao.deleteCredentials()
    }

    override fun setUser(user: AuthUserRecord) {
        dao.upsertUser(
            AuthUserEntity(
                userId = user.userId,
                email = user.email,
                phone = user.phone,
                name = user.name,
            ),
        )
    }

    override fun deleteUser() {
        dao.deleteUser()
    }
}
