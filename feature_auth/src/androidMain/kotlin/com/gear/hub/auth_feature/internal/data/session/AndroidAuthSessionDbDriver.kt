package com.gear.hub.auth_feature.internal.data.session

import android.content.Context
import com.gear.hub.data.config.DatabaseRuntime
import com.gear.hub.data.config.DatabaseConfig
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper

/**
 * Драйвер доступа к таблице сессии на Android, использующий базовую
 * инфраструктуру data_service: имя БД, версию и пароль берём из
 * [DatabaseRuntime.config].
 */
internal class AndroidAuthSessionDbDriver(
    private val runtime: DatabaseRuntime,
) : AuthSessionDbDriver {

    /**
     * Помощник, создающий таблицу статуса авторизации в шифрованной базе.
     */
    private val dbHelper by lazy(LazyThreadSafetyMode.NONE) {
        AuthSessionDbHelper(runtime.context.context, runtime.config)
    }

    /**
     * Открытая ссылка на шифрованную базу; создаётся лениво и переиспользуется.
     */
    private val database: SQLiteDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        SQLiteDatabase.loadLibs(runtime.context.context)
        dbHelper.getWritableDatabase(runtime.config.passphrase.toCharArray())
    }

    override fun ensureInitialized() {
        dbHelper.ensureSchema(database)
    }

    override fun readAuthorized(): Boolean = dbHelper.readAuthorized(database)

    override fun writeAuthorized(value: Boolean) {
        dbHelper.writeAuthorized(database, value)
    }
}

/**
 * SQLiteOpenHelper для таблицы авторизации в общей шифрованной базе.
 */
private class AuthSessionDbHelper(
    private val appContext: Context,
    private val config: DatabaseConfig,
) : SQLiteOpenHelper(appContext, config.name, null, config.version) {

    override fun onCreate(db: SQLiteDatabase) {
        ensureSchema(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Версия оставлена для будущих миграций конкретной фичи.
    }

    /**
     * Гарантирует создание таблицы и дефолтной записи.
     */
    fun ensureSchema(db: SQLiteDatabase) {
        db.execSQL(AuthSessionQueries.CREATE_TABLE)
        db.execSQL(AuthSessionQueries.INSERT_DEFAULT)
    }

    /**
     * Читает флаг авторизации.
     */
    fun readAuthorized(db: SQLiteDatabase): Boolean {
        val cursor = db.rawQuery(AuthSessionQueries.SELECT_AUTHORIZED, emptyArray())
        cursor.use {
            if (it.moveToFirst()) {
                return it.getInt(0) == 1
            }
        }
        writeAuthorized(db, value = false)
        return false
    }

    /**
     * Обновляет флаг авторизации.
     */
    fun writeAuthorized(db: SQLiteDatabase, value: Boolean) {
        db.execSQL(AuthSessionQueries.UPDATE_AUTHORIZED, arrayOf(if (value) 1 else 0))
    }
}

/**
 * Фабрика платформенного драйвера.
 */
internal actual fun createAuthSessionDbDriver(runtime: DatabaseRuntime): AuthSessionDbDriver =
    AndroidAuthSessionDbDriver(runtime)
