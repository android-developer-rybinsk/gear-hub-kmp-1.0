package com.gear.hub.auth_feature.internal.data.session

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileProtectionComplete
import platform.Foundation.NSFileProtectionKey
import platform.Foundation.NSString
import platform.Foundation.NSUserDomainMask
import platform.Foundation.URLByAppendingPathComponent
import platform.Foundation.create
import platform.Foundation.setAttributes
import platform.Foundation.stringByResolvingSymlinksInPath
import platform.SQLite3.SQLITE_DONE
import platform.SQLite3.SQLITE_OK
import platform.SQLite3.SQLITE_ROW
import platform.SQLite3.sqlite3
import platform.SQLite3.sqlite3_close
import platform.SQLite3.sqlite3_column_int
import platform.SQLite3.sqlite3_exec
import platform.SQLite3.sqlite3_finalize
import platform.SQLite3.sqlite3_open
import platform.SQLite3.sqlite3_prepare_v2
import platform.SQLite3.sqlite3_reset
import platform.SQLite3.sqlite3_step
import platform.SQLite3.sqlite3_stmt

/**
 * iOS-хранилище статуса авторизации на базе локальной БД SQLite с защитой файла.
 *
 * Вместо небезопасного текстового файла используется база `auth_session.db`, лежащая
 * в каталоге Application Support и защищённая `NSFileProtectionComplete`.
 * Таблица содержит единственную запись с признаком авторизации, чтение/запись
 * выполняются в фоновых потоках.
 */
class IosAuthSessionStorage : AuthSessionStorage {

    /**
     * Полный путь к базе данных, создаваемой в Application Support.
     */
    private val databasePath: String by lazy(LazyThreadSafetyMode.NONE) {
        val supportDir = NSFileManager.defaultManager.URLForDirectory(
            directory = NSApplicationSupportDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        ) ?: error("Не удалось получить путь ApplicationSupport для базы авторизации")
        supportDir.stringByResolvingSymlinksInPath!!.URLByAppendingPathComponent(DB_NAME).path!!
    }

    override suspend fun isAuthorized(): Boolean = withContext(Dispatchers.Default) {
        withDatabase { db ->
            ensureTable(db)
            val stmt = prepare(db, "SELECT authorized FROM auth_session WHERE id = 1")
            try {
                val step = sqlite3_step_safe(stmt)
                if (step == SQLITE_ROW) {
                    sqlite3_column_int(stmt, 0) == 1
                } else {
                    false
                }
            } finally {
                sqlite3_finalize(stmt)
                sqlite3_reset(stmt)
            }
        }
    }

    override suspend fun setAuthorized(value: Boolean) {
        withContext(Dispatchers.Default) {
            withDatabase { db ->
                ensureTable(db)
                exec(db, "UPDATE auth_session SET authorized = ${if (value) 1 else 0} WHERE id = 1")
            }
        }
    }

    /**
     * Открывает или создаёт базу, гарантируя защиту файла и корректное закрытие соединения.
     */
    private inline fun <T> withDatabase(block: (CPointer<sqlite3>) -> T): T = memScoped {
        val dbPtr = alloc<CPointerVar<sqlite3>>()
        val openResult = sqlite3_open(databasePath, dbPtr.ptr)
        if (openResult != SQLITE_OK) {
            error("Не удалось открыть базу авторизации: код $openResult")
        }
        protectFile()
        val db = dbPtr.value ?: error("Ссылка на базу авторизации пуста")
        try {
            block(db)
        } finally {
            sqlite3_close(db)
        }
    }

    /**
     * Создаёт таблицу статуса и дефолтную запись, если база только создана.
     */
    private fun ensureTable(db: CPointer<sqlite3>) {
        exec(
            db,
            """
            CREATE TABLE IF NOT EXISTS auth_session (
                id INTEGER PRIMARY KEY CHECK (id = 1),
                authorized INTEGER NOT NULL DEFAULT 0
            );
            """.trimIndent(),
        )
        exec(db, "INSERT OR IGNORE INTO auth_session(id, authorized) VALUES(1, 0)")
    }

    /**
     * Выполняет произвольный SQL без выборки и проверяет код возврата.
     */
    private fun exec(db: CPointer<sqlite3>, sql: String) {
        val errorPtr = memScoped { alloc<CPointerVar<ByteVar>>() }
        val code = sqlite3_exec(db, sql, null, null, errorPtr.ptr)
        if (code != SQLITE_OK) {
            val message = errorPtr.value?.toKString() ?: "Неизвестная ошибка"
            throw IllegalStateException("Ошибка SQLite ($code): $message")
        }
    }

    /**
     * Готовит SQL-выражение и возвращает statement.
     */
    private fun prepare(db: CPointer<sqlite3>, sql: String): CPointer<sqlite3_stmt> = memScoped {
        val stmtPtr = alloc<CPointerVar<sqlite3_stmt>>()
        val code = sqlite3_prepare_v2(db, sql, sql.length, stmtPtr.ptr, null)
        if (code != SQLITE_OK) {
            throw IllegalStateException("Не удалось подготовить запрос: код $code")
        }
        stmtPtr.value ?: error("Statement не создан")
    }

    /**
     * Выполняет шаг statement и проверяет ошибки.
     */
    private fun sqlite3_step_safe(stmt: CPointer<sqlite3_stmt>): Int {
        val result = sqlite3_step(stmt)
        if (result !in listOf(SQLITE_ROW, SQLITE_DONE)) {
            throw IllegalStateException("Ошибка выполнения запроса: код $result")
        }
        return result
    }

    /**
     * Устанавливает защиту файла базы NSFileProtectionComplete.
     */
    private fun protectFile() {
        val manager = NSFileManager.defaultManager
        manager.setAttributes(
            mapOf(NSFileProtectionKey to NSFileProtectionComplete),
            databasePath,
            null,
        )
    }

    private companion object {
        /**
         * Имя файла БД, общей для iOS-платформы.
         */
        private const val DB_NAME = "auth_session.db"
    }
}
