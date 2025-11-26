package com.gear.hub.auth_feature.internal.data.session

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

/**
 * DAO, инкапсулирующее запросы из [AuthSessionQueries].
 */
@Dao
internal interface AuthSessionDao {
    @Upsert
    fun upsertCredentials(entity: AuthCredentialsEntity)

    @Upsert
    fun upsertUser(entity: AuthUserEntity)

    @Query(AuthSessionQueries.SELECT_CREDENTIALS)
    fun getCredentials(): AuthCredentialsEntity?

    @Query(AuthSessionQueries.SELECT_USER)
    fun getUser(): AuthUserEntity?

    @Query(AuthSessionQueries.DELETE_CREDENTIALS)
    fun deleteCredentials()

    @Query(AuthSessionQueries.DELETE_USER)
    fun deleteUser()
}
