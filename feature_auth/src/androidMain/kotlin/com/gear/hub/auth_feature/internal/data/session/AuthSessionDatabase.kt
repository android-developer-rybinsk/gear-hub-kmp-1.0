package com.gear.hub.auth_feature.internal.data.session

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
    version = 2,
    exportSchema = false,
)
internal abstract class AuthSessionDatabase : RoomDatabase() {
    abstract fun authSessionDao(): AuthSessionDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS auth_credentials_new (
                        id INTEGER PRIMARY KEY CHECK (id = 1),
                        accessToken TEXT NOT NULL,
                        refreshToken TEXT NOT NULL,
                        expiresIn INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    """
                    INSERT INTO auth_credentials_new (id, accessToken, refreshToken, expiresIn)
                    SELECT id, access_token, refresh_token, expires_in FROM auth_credentials
                    """.trimIndent(),
                )
                database.execSQL("DROP TABLE auth_credentials")
                database.execSQL("ALTER TABLE auth_credentials_new RENAME TO auth_credentials")

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS auth_user_new (
                        id INTEGER PRIMARY KEY CHECK (id = 1),
                        userId TEXT NOT NULL,
                        email TEXT,
                        phone TEXT,
                        name TEXT NOT NULL
                    )
                    """.trimIndent(),
                )
                database.execSQL(
                    """
                    INSERT INTO auth_user_new (id, userId, email, phone, name)
                    SELECT id, user_id, email, phone, name FROM auth_user
                    """.trimIndent(),
                )
                database.execSQL("DROP TABLE auth_user")
                database.execSQL("ALTER TABLE auth_user_new RENAME TO auth_user")
            }
        }
    }
}
