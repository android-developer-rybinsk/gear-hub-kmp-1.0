package com.gear.hub.auth_feature.internal.data.session

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper

/**
 * Android-реализация хранилища статуса авторизации на базе собственной шифрованной БД (SQLCipher).
 *
 * Здесь не используются SharedPreferences: флаг хранится в таблице `auth_session`,
 * шифрование обеспечивается SQLCipher через пароль, известный только приложению.
 */
class AndroidAuthSessionStorage(
    private val context: Context,
) : AuthSessionStorage {

    /**
     * Помощник для работы с шифрованной базой с единственной таблицей состояния сессии.
     */
    private val dbHelper by lazy(LazyThreadSafetyMode.NONE) { AuthSessionDbHelper(context) }

    /**
     * Открытая ссылка на шифрованную базу; создаётся один раз и переиспользуется.
     */
    private val database: SQLiteDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        SQLiteDatabase.loadLibs(context)
        dbHelper.getWritableDatabase(PASSPHRASE)
    }

    override suspend fun isAuthorized(): Boolean = withContext(Dispatchers.IO) {
        dbHelper.readAuthorized(database)
    }

    override suspend fun setAuthorized(value: Boolean) {
        withContext(Dispatchers.IO) { dbHelper.writeAuthorized(database, value) }
    }

    /**
     * Локальный SQLiteOpenHelper под SQLCipher, создающий таблицу для хранения статуса.
     */
    private class AuthSessionDbHelper(
        private val appContext: Context,
    ) : SQLiteOpenHelper(appContext, DB_NAME, null, DB_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS auth_session (
                    id INTEGER PRIMARY KEY CHECK (id = 1),
                    authorized INTEGER NOT NULL DEFAULT 0
                );
                """.trimIndent(),
            )
            db.execSQL("INSERT OR IGNORE INTO auth_session(id, authorized) VALUES(1, 0)")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // Для текущей версии ничего не требуется; версия оставлена для совместимости миграций.
        }

        /**
         * Читает значение флага авторизации из таблицы.
         */
        fun readAuthorized(db: SQLiteDatabase): Boolean {
            val cursor = db.rawQuery("SELECT authorized FROM auth_session WHERE id = 1", emptyArray())
            cursor.use {
                if (it.moveToFirst()) {
                    return it.getInt(0) == 1
                }
            }
            // Если по какой-то причине записи нет, создаём дефолт.
            writeAuthorized(db, value = false)
            return false
        }

        /**
         * Обновляет флаг авторизации.
         */
        fun writeAuthorized(db: SQLiteDatabase, value: Boolean) {
            db.execSQL(
                "UPDATE auth_session SET authorized = ? WHERE id = 1",
                arrayOf(if (value) 1 else 0),
            )
        }
    }

    private companion object {
        /**
         * Имя шифрованного файла базы данных.
         */
        private const val DB_NAME = "auth_session.db"

        /**
         * Версия схемы для будущих миграций.
         */
        private const val DB_VERSION = 1

        /**
         * Пароль для SQLCipher: на практике должен генерироваться/храниться безопасно,
         * здесь зашит константой, чтобы исключить использование SharedPreferences.
         */
        private val PASSPHRASE = "gearhub_auth_cipher".toCharArray()
    }
}
