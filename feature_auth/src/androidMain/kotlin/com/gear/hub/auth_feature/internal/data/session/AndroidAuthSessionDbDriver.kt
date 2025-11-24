package com.gear.hub.auth_feature.internal.data.session

import androidx.room.Database
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import com.gear.hub.data.config.DatabaseRuntime

/**
 * Драйвер доступа к таблице сессии на Android, опирающийся на Room и
 * шифрование SQLCipher, подготовленное в data_service. Все операции
 * выполняются через DAO, чтобы минимизировать ручной SQL в платформенном слое.
 */
internal class AndroidAuthSessionDbDriver(
    runtime: DatabaseRuntime,
) : AuthSessionDbDriver {

    /**
     * Единый Room-инстанс с SQLCipher, создаётся через базовый runtime.
     */
    private val database: AuthSessionDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        runtime.roomDatabaseBuilder(AuthSessionDatabase::class.java)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL(AuthSessionQueries.CREATE_TABLE)
                    db.execSQL(AuthSessionQueries.INSERT_DEFAULT)
                }
            })
            .build()
    }

    private val dao: AuthSessionDao by lazy(LazyThreadSafetyMode.NONE) { database.authSessionDao() }

    override fun ensureInitialized() {
        dao.insertDefault(AuthSessionEntity())
    }

    override fun getAuthorized(): Boolean {
        return dao.getAuthorizedFlag() == 1
    }

    override fun setAuthorized(value: Boolean) {
        dao.setAuthorizedFlag(if (value) 1 else 0)
    }
}

/**
 * Room-сущность для таблицы авторизации.
 */
@androidx.room.Entity(tableName = "auth_session")
internal data class AuthSessionEntity(
    @androidx.room.PrimaryKey val id: Int = 1,
    @androidx.room.ColumnInfo(name = "authorized") val authorized: Int = 0,
)

/**
 * DAO, инкапсулирующее запросы из [AuthSessionQueries].
 */
@Dao
internal interface AuthSessionDao {
    @Query(AuthSessionQueries.SELECT_AUTHORIZED)
    fun getAuthorizedFlag(): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDefault(entity: AuthSessionEntity)

    @Query(AuthSessionQueries.UPDATE_AUTHORIZED)
    fun setAuthorizedFlag(value: Int)
}

/**
 * RoomDatabase, собирающая DAO и схему.
 */
@Database(
    entities = [AuthSessionEntity::class],
    version = 1,
    exportSchema = false,
)
internal abstract class AuthSessionDatabase : RoomDatabase() {
    abstract fun authSessionDao(): AuthSessionDao
}

/**
 * Фабрика платформенного драйвера.
 */
internal actual fun createAuthSessionDbDriver(runtime: DatabaseRuntime): AuthSessionDbDriver =
    AndroidAuthSessionDbDriver(runtime)
