package com.gear.hub.auth_feature.internal.data.session

import co.touchlab.sqliter.interop.CPointerVarOf
import co.touchlab.sqliter.interop.SqliteStatementPointer
import co.touchlab.sqliter.interop.sqlite3
import co.touchlab.sqliter.interop.sqlite3_close
import co.touchlab.sqliter.interop.sqlite3_column_int
import co.touchlab.sqliter.interop.sqlite3_column_text
import co.touchlab.sqliter.interop.sqlite3_exec
import co.touchlab.sqliter.interop.sqlite3_finalize
import co.touchlab.sqliter.interop.sqlite3_open
import co.touchlab.sqliter.interop.sqlite3_prepare_v2
import co.touchlab.sqliter.interop.sqlite3_reset
import co.touchlab.sqliter.interop.sqlite3_step
import com.gear.hub.auth_feature.api.session.AuthSessionDbDriver
import com.gear.hub.auth_feature.api.session.AuthCredentialsRecord
import com.gear.hub.auth_feature.api.session.AuthUserRecord
import com.gear.hub.data.config.DatabaseFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
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

/**
 * iOS-драйвер для таблицы авторизации. Использует SQLite из Application Support
 * и базовые параметры БД из [DatabaseFactory.config].
 */
internal class IosAuthSessionDbDriver(
    factory: DatabaseFactory,
) : AuthSessionDbDriver {

    /**
     * Базовые коды SQLite для проверки результата вызовов.
     */
    private companion object {
        const val SQLITE_OK_CODE: Int = 0
        const val SQLITE_ROW_CODE: Int = 100
        const val SQLITE_DONE_CODE: Int = 101
        const val UPSERT_CREDENTIALS_SQL =
            "INSERT OR REPLACE INTO auth_credentials(id, access_token, refresh_token, expires_in) VALUES(1, :accessToken, :refreshToken, :expiresIn)"
        const val UPSERT_USER_SQL =
            "INSERT OR REPLACE INTO auth_user(id, user_id, email, phone, name) VALUES(1, :userId, :email, :phone, :name)"
    }

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

    private val initScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val initJob = lazy {
        initScope.async(start = CoroutineStart.LAZY) {
            withDatabase { db ->
                exec(db, AuthSessionQueries.CREATE_TABLE_CREDENTIALS)
                exec(db, AuthSessionQueries.CREATE_TABLE_USER)
            }
        }
    }

    override suspend fun ensureInitialized() {
        initJob.value.await()
    }

    override fun setCredentials(credentials: AuthCredentialsRecord) {
        withDatabase { db ->
            val sql = UPSERT_CREDENTIALS_SQL
                .replace(":accessToken", "'${credentials.accessToken}'")
                .replace(":refreshToken", "'${credentials.refreshToken}'")
                .replace(":expiresIn", credentials.expiresIn.toString())
            exec(db, sql)
        }
    }

    override fun getCredentials(): AuthCredentialsRecord? =
        withDatabase { db ->
            val stmt = prepare(db, AuthSessionQueries.SELECT_CREDENTIALS)
            try {
                val step = sqlite3_step_safe(stmt)
                if (step == SQLITE_ROW_CODE) {
                    val accessToken = sqlite3_column_text(stmt, 1)?.toKString()
                    val refreshToken = sqlite3_column_text(stmt, 2)?.toKString()
                    val expiresIn = sqlite3_column_int(stmt, 3).toLong()
                    if (accessToken != null && refreshToken != null) {
                        AuthCredentialsRecord(accessToken, refreshToken, expiresIn)
                    } else {
                        null
                    }
                } else {
                    null
                }
            } finally {
                sqlite3_finalize(stmt)
                sqlite3_reset(stmt)
            }
        }

    override fun deleteCredentials() {
        withDatabase { db ->
            exec(db, AuthSessionQueries.DELETE_CREDENTIALS)
        }
    }

    override fun setUser(user: AuthUserRecord) {
        withDatabase { db ->
            val sql = UPSERT_USER_SQL
                .replace(":userId", "'${user.userId}'")
                .replace(":email", user.email?.let { "'$it'" } ?: "NULL")
                .replace(":phone", user.phone?.let { "'$it'" } ?: "NULL")
                .replace(":name", "'${user.name}'")
            exec(db, sql)
        }
    }

    override fun deleteUser() {
        withDatabase { db ->
            exec(db, AuthSessionQueries.DELETE_USER)
        }
    }

    /**
     * Открывает/создаёт базу и гарантирует закрытие.
     */
    private inline fun <T> withDatabase(block: (CPointer<sqlite3>) -> T): T = memScoped {
        val dbPtr: CPointerVar<sqlite3> = alloc<CPointerVarOf<sqlite3>>()
        val openResult = sqlite3_open(databasePath, dbPtr.ptr)
        if (openResult != SQLITE_OK_CODE) {
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
        if (code != SQLITE_OK_CODE) {
            val message = errorPtr.value?.toKString() ?: "Неизвестная ошибка"
            throw IllegalStateException("Ошибка SQLite ($code): $message")
        }
    }

    /**
     * Готовит statement.
     */
    private fun prepare(db: CPointer<sqlite3>, sql: String): CPointer<SqliteStatementPointer> = memScoped {
        val stmtPtr = alloc<CPointerVar<SqliteStatementPointer>>()
        val code = sqlite3_prepare_v2(db, sql, sql.length, stmtPtr.ptr, null)
        if (code != SQLITE_OK_CODE) {
            throw IllegalStateException("Не удалось подготовить запрос: код $code")
        }
        stmtPtr.value ?: error("Statement не создан")
    }

    /**
     * Проверяет результат выполнения statement.
     */
    private fun sqlite3_step_safe(stmt: CPointer<SqliteStatementPointer>): Int {
        val result = sqlite3_step(stmt)
        if (result !in listOf(SQLITE_ROW_CODE, SQLITE_DONE_CODE)) {
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

