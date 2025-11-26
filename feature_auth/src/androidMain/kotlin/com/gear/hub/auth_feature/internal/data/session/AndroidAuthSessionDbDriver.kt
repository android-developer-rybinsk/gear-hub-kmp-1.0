package com.gear.hub.auth_feature.internal.data.session

import androidx.room.Database
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gear.hub.auth_feature.api.session.AuthSessionDbDriver
import com.gear.hub.auth_feature.api.session.AuthCredentialsRecord
import com.gear.hub.auth_feature.api.session.AuthUserRecord
import com.gear.hub.data.config.DatabaseFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async

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
            .addMigrations(*AuthSessionMigrations.ALL)
            .build()
    }

    private val dao: AuthSessionDaoV2 by lazy(LazyThreadSafetyMode.NONE) { database.authSessionDao() }

    private val initScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val initJob = lazy {
        initScope.async(start = CoroutineStart.LAZY) { /* Room создаст таблицы лениво */ }
    }

    override suspend fun ensureInitialized() {
        initJob.value.await()
    }

    override fun setCredentials(credentials: AuthCredentialsRecord) {
        dao.setCredentials(
            accessToken = credentials.accessToken,
            refreshToken = credentials.refreshToken,
            expiresIn = credentials.expiresIn,
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
        dao.setUser(
            userId = user.userId,
            email = user.email,
            phone = user.phone,
            name = user.name,
        )
    }

    override fun deleteUser() {
        dao.deleteUser()
    }
}

/**
 * Таблица для хранения токенов в зашифрованной БД.
 */
@androidx.room.Entity(tableName = "auth_credentials")
internal data class AuthCredentialsEntity(
    @androidx.room.PrimaryKey val id: Int = 1,
    @androidx.room.ColumnInfo(name = "access_token") val accessToken: String,
    @androidx.room.ColumnInfo(name = "refresh_token") val refreshToken: String,
    @androidx.room.ColumnInfo(name = "expires_in") val expiresIn: Long,
)

/**
 * Таблица с данными пользователя из ответа регистрации.
 */
@androidx.room.Entity(tableName = "auth_user")
internal data class AuthUserEntity(
    @androidx.room.PrimaryKey val id: Int = 1,
    @androidx.room.ColumnInfo(name = "user_id") val userId: String,
    @androidx.room.ColumnInfo(name = "email") val email: String?,
    @androidx.room.ColumnInfo(name = "phone") val phone: String?,
    @androidx.room.ColumnInfo(name = "name") val name: String,
)

/**
 * DAO, инкапсулирующее запросы из [AuthSessionQueries].
 */
@Dao
internal interface AuthSessionDaoV2 {
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

/**
 * RoomDatabase, собирающая DAO и схему.
 */
/**
 * Шифрованная база сессии авторизации (токены, пользователь).
 */
@Database(
    entities = [AuthCredentialsEntity::class, AuthUserEntity::class],
    version = 3,
    exportSchema = false,
)
internal abstract class AuthSessionDatabase : RoomDatabase() {
    abstract fun authSessionDao(): AuthSessionDaoV2
}

/**
 * Миграции схемы Room для таблиц авторизации.
 */
internal object AuthSessionMigrations {
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(AuthSessionQueries.CREATE_TABLE_CREDENTIALS)
            db.execSQL(AuthSessionQueries.CREATE_TABLE_USER)
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS auth_session")
        }
    }

    val ALL = arrayOf(MIGRATION_1_2, MIGRATION_2_3)
}

