package com.gear.hub.auth_feature.internal.data.session

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSDataWritingAtomic
import platform.Foundation.NSDataWritingFileProtectionComplete
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.stringByAppendingPathComponent
import platform.Foundation.writeToURL
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

/**
 * iOS-реализация хранилища статуса авторизации на базе собственного зашифрованного файла.
 *
 * Вместо UserDefaults используется отдельный файл с защитой `NSFileProtectionComplete`,
 * что даёт шифрование на уровне файловой системы и недоступность без разблокировки устройства.
 */
class IosAuthSessionStorage : AuthSessionStorage {

    /**
     * Путь к файлу с флагом авторизации в песочнице приложения.
     */
    private val fileUrl: NSURL by lazy(LazyThreadSafetyMode.NONE) {
        val documentsDir = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        ) ?: error("Не удалось получить путь к DocumentDirectory")
        documentsDir.URLByAppendingPathComponent(FILE_NAME)
    }

    override suspend fun isAuthorized(): Boolean = withContext(Dispatchers.Default) {
        val data = NSData.dataWithContentsOfURL(fileUrl)
        val stored = data?.let { NSString.create(it, NSUTF8StringEncoding) as String? }
        stored == AUTH_TRUE
    }

    override suspend fun setAuthorized(value: Boolean) {
        withContext(Dispatchers.Default) {
            val text = if (value) AUTH_TRUE else AUTH_FALSE
            val data = (text as NSString).dataUsingEncoding(NSUTF8StringEncoding)
                ?: error("Не удалось сериализовать флаг авторизации")
            val options = NSDataWritingAtomic or NSDataWritingFileProtectionComplete
            if (!data.writeToURL(fileUrl, options)) {
                error("Не удалось сохранить флаг авторизации в файл")
            }
        }
    }

    private companion object {
        /**
         * Имя файла с флагом авторизации.
         */
        private const val FILE_NAME = "auth_session.flag"

        /**
         * Значения, сохраняемые в файле.
         */
        private const val AUTH_TRUE = "1"
        private const val AUTH_FALSE = "0"
    }
}
