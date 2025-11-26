package com.gear.hub.auth_feature.internal.data.session

import androidx.room.Dao
import androidx.room.Query

/**
 * DAO, инкапсулирующее запросы из [AuthSessionQueries].
 */
@Dao
internal interface AuthSessionDao {
    @Query(AuthSessionQueries.UPSERT_CREDENTIALS)
    fun setCredentials(
        accessToken: String,
        refreshToken: String,
        expiresIn: Long,
    )

    @Query(AuthSessionQueries.UPSERT_USER)
    fun setUser(
        userId: String,
        email: String?,
        phone: String?,
        name: String,
    )

    @Query(AuthSessionQueries.SELECT_CREDENTIALS)
    fun getCredentials(): AuthCredentialsEntity?

    @Query(AuthSessionQueries.SELECT_USER)
    fun getUser(): AuthUserEntity?

    @Query(AuthSessionQueries.DELETE_CREDENTIALS)
    fun deleteCredentials()

    @Query(AuthSessionQueries.DELETE_USER)
    fun deleteUser()
}
