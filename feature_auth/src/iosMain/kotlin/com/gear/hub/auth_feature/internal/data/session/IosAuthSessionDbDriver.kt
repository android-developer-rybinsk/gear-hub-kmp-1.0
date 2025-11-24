@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.gear.hub.auth_feature.internal.data.session

import com.gear.hub.data.config.DatabaseFactory
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileProtectionComplete
import platform.Foundation.NSFileProtectionKey
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.setAttributes
import sqlite3.SQLITE_DONE
import sqlite3.SQLITE_OK
import sqlite3.SQLITE_ROW
import sqlite3.sqlite3
import sqlite3.sqlite3_close
import sqlite3.sqlite3_column_int
import sqlite3.sqlite3_exec
import sqlite3.sqlite3_finalize
import sqlite3.sqlite3_open
import sqlite3.sqlite3_prepare_v2
import sqlite3.sqlite3_reset
import sqlite3.sqlite3_step
import sqlite3.sqlite3_stmt

/**
 * iOS-драйвер для таблицы авторизации. Использует SQLite из Application Support
 * и базовые параметры БД из [DatabaseFactory.config].
 */
internal class IosAuthSessionDbDriver(
    factory: DatabaseFactory,
) : AuthSessionDbDriver {

    /**
     * Полный путь к базе авторизации, общая для проекта KMP.
     */
    private val databasePath: String = run {
        val manager = NSFileManager.defaultManager
        val supportDir: NSURL = manager.URLForDirectory(
            directory = NSApplicationSupportDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        ) ?: error("Не удалось получить путь ApplicationSupport для базы авторизации")

        val resolvedUrl = supportDir.URLByAppendingPathComponent(factory.config.name, isDirectory = false)
            ?: error("Не удалось сформировать путь базы авторизации")
        resolvedUrl.path ?: error("Путь к базе авторизации пуст")
    }

    override fun ensureInitialized() {
        withDatabase { db ->
            exec(db, AuthSessionQueries.CREATE_TABLE)
            exec(db, AuthSessionQueries.CREATE_TABLE_CREDENTIALS)
            exec(db, AuthSessionQueries.CREATE_TABLE_USER)
            exec(db, AuthSessionQueries.INSERT_DEFAULT)
        }
    }

    override fun getAuthorized(): Boolean =
        withDatabase { db ->
            val stmt = prepare(db, AuthSessionQueries.SELECT_AUTHORIZED)
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

    override fun setAuthorized(value: Boolean) {
        withDatabase { db ->
            exec(db, AuthSessionQueries.UPDATE_AUTHORIZED.replace(":value", if (value) "1" else "0"))
        }
    }

    override fun setCredentials(credentials: AuthCredentialsRecord) {
        withDatabase { db ->
            val sql = AuthSessionQueries.UPSERT_CREDENTIALS
                .replace(":accessToken", "'${credentials.accessToken}'")
                .replace(":refreshToken", "'${credentials.refreshToken}'")
                .replace(":expiresIn", credentials.expiresIn.toString())
            exec(db, sql)
        }
    }

    override fun setUser(user: AuthUserRecord) {
        withDatabase { db ->
            val sql = AuthSessionQueries.UPSERT_USER
                .replace(":userId", "'${user.userId}'")
                .replace(":email", user.email?.let { "'$it'" } ?: "NULL")
                .replace(":phone", user.phone?.let { "'$it'" } ?: "NULL")
                .replace(":name", "'${user.name}'")
            exec(db, sql)
        }
    }

    /**
     * Открывает/создаёт базу и гарантирует закрытие.
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
     * Выполняет SQL без выборки.
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
     * Готовит statement.
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
     * Проверяет результат выполнения statement.
     */
    private fun sqlite3_step_safe(stmt: CPointer<sqlite3_stmt>): Int {
        val result = sqlite3_step(stmt)
        if (result !in listOf(SQLITE_ROW, SQLITE_DONE)) {
            throw IllegalStateException("Ошибка выполнения запроса: код $result")
        }
        return result
    }

    /**
     * Включает защиту файла базы.
     */
    private fun protectFile() {
        val manager = NSFileManager.defaultManager
        manager.setAttributes(
            mapOf(NSFileProtectionKey to NSFileProtectionComplete),
            databasePath,
            null,
        )
    }
}

/**
 * Фабрика платформенного драйвера.
 */
internal actual fun createAuthSessionDbDriver(factory: DatabaseFactory): AuthSessionDbDriver =
    IosAuthSessionDbDriver(factory)
