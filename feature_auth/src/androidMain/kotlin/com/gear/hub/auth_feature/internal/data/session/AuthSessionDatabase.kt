package com.gear.hub.auth_feature.internal.data.session

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Таблица для хранения токенов в зашифрованной БД.
 */
@androidx.room.Entity(tableName = "auth_credentials")
internal data class AuthCredentialsEntity(
    @androidx.room.PrimaryKey val id: Int = 1,
    @androidx.room.ColumnInfo(name = "accessToken") val accessToken: String,
    @androidx.room.ColumnInfo(name = "refreshToken") val refreshToken: String,
    @androidx.room.ColumnInfo(name = "expiresIn") val expiresIn: Long,
)

/**
 * Таблица с данными пользователя из ответа регистрации.
 */
@androidx.room.Entity(tableName = "auth_user")
internal data class AuthUserEntity(
    @androidx.room.PrimaryKey val id: Int = 1,
    @androidx.room.ColumnInfo(name = "userId") val userId: String,
    @androidx.room.ColumnInfo(name = "email") val email: String?,
    @androidx.room.ColumnInfo(name = "phone") val phone: String?,
    @androidx.room.ColumnInfo(name = "name") val name: String,
)

/**
 * Шифрованная база сессии авторизации (токены, пользователь).
 */
@Database(
    entities = [AuthCredentialsEntity::class, AuthUserEntity::class],
    version = 1,
    exportSchema = false,
)
internal abstract class AuthSessionDatabase : RoomDatabase() {
    abstract fun authSessionDao(): AuthSessionDao
}
