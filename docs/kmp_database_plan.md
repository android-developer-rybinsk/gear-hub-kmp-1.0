# План интеграции БД для KMP

## Цели
- Использовать единый механизм создания и шифрования БД из `data_service`, чтобы все фичи повторно применяли базовую конфигурацию.
- Соблюдать Clean Architecture: слой data фичи описывает таблицы/DAO и хранение, общая инфраструктура лежит в `data_service`.
- Шифрование на Android через Room + SQLCipher; на iOS — SQLite/SQLCipher (через cinterop), при этом запросы и интерфейсы остаются общими.

## Базовый уровень (`data_service`)
1. **Конфигурация**: `DatabaseConfig` задаёт имя файла, пароль и версию БД. Пароль хранится/получается из безопасного стора (Android Keystore; аналогичный механизм для iOS).
2. **Инициализация**: `EncryptedDatabaseFactory` получает `PlatformContext` и `DatabaseConfig`, создаёт `DatabaseFactory` и вызывает все зарегистрированные инициализаторы из `DatabaseRegistry`.
3. **Платформенные фабрики**:
   - **Android**: `DatabaseFactory.roomDatabaseBuilder(dbClass)` создаёт `RoomDatabase.Builder` с `SupportFactory(passphrase)` (SQLCipher). Фича передаёт сюда свои сущности/DAO и миграции.
   - **iOS**: `DatabaseFactory` пока содержит конфиг. Фича использует общий набор SQL/DAO и платформенный драйвер (sqlite3/sqlcipher) для фактического чтения/записи. Инициализатор вызывается из реестра так же, как на Android.
4. **Регистрация схем**: каждая фича вызывает `DatabaseRegistry.registerModule(moduleName) { factory -> ... }` и в этом блоке собирает свою БД/DAO.

## Уровень фичи (пример `feature_auth`)
1. **Слой data**:
   - Объявляет модели таблиц и DAO (Room аннотации на Android). SQL запросы должны быть в отдельном файле (например, `AuthSessionQueries.kt`).
   - Для iOS использует тот же набор запросов через общий интерфейс `AuthSessionDbDriver`, но платформа предоставляет драйвер на sqlite3/sqlcipher.
   - Терминология `set/get`: DAO/драйвер предоставляет `setAuthSession(...)` и `getAuthSession()` вместо write/read.
2. **Инициализация**:
   - В модуле Koin фичи регистрируется `DatabaseRegistry`-инициализатор: `databaseRegistry.registerModule("auth") { factory -> ... }`.
   - **Android**: внутри инициализатора вызывается `factory.roomDatabaseBuilder(AuthDatabase::class.java)` с миграциями; DAO создаётся через Room и сохраняется в DI.
   - **iOS**: внутри инициализатора создаётся драйвер на базе sqlite3/sqlcipher, используя путь и пароль из `DatabaseConfig`; регистрация DAO/репозиториев происходит в DI один раз для обеих платформ.
3. **Шифрование ключа**:
   - Android: пароль БД хранится в Keystore (либо генерируется и кэшируется в EncryptedSharedPreferences, либо получаем из `DatabaseConfig.passphrase` уже защищённым способом).
   - iOS: пароль хранится в защищённом Keychain/KeyStore iOS; в `DatabaseConfig.passphrase` должен приходить итоговый ключ.
4. **Миграции**: фича несёт ответственность за миграции Room/SQL и версию (`DatabaseConfig.version`). В реестре можно обновлять версии без затрагивания других модулей.

## Навигация и инициализация
1. `data_service` поднимается при старте (Koin модуль): создаётся `EncryptedDatabaseFactory` с `PlatformContext`, инициализируются все зарегистрированные фичевые модули.
2. Фича авторизации сохраняет токены/профиль в своей зашифрованной таблице. При успешной авторизации/регистрации данные пишутся в БД и используются для проверки авторизованного состояния на старте (Splash → Menu).

## Практические шаги для текущего проекта
1. **Укрепить `data_service`**: убедиться, что `EncryptedDatabaseFactory` и `DatabaseRegistry` инициализируются до фич; хранение ключа в Keystore/Keychain; добавить Koin биндинги для `DatabaseConfig` и `PlatformContext`.
2. **Android Room**:
   - В `feature_auth` подключить Room entities/DAO с аннотациями и миграциями; убрать прямые SQLite вызовы.
   - Использовать `factory.roomDatabaseBuilder(...)` в инициализаторе реестра, передав `passphrase` для SQLCipher.
3. **iOS sqlite/sqlcipher**:
   - Подключить cinterop или sqlcipher/sqlite3 зависимость, использовать общий драйвер и запросы из `AuthSessionQueries` без дублирования логики. Код запросов общий; платформа предоставляет только драйвер/путь.
4. **Общий API**: DAO/репозитории должны быть описаны в `commonMain` (интерфейсы), а реализации — в `androidMain`/`iosMain`, но с единым набором SQL/моделей из `commonMain`.
5. **Тестирование**: покрыть unit тестами общих запросов и интеграционными тестами Room на Android/iOS-симуляторе для проверки шифрования/миграций.

## Ориентиры из статьи
- Поддерживать единый контракт и схемы в `commonMain`, а платформенные специфики — в `actual`-частях (драйвер, путь к файлу, шифрование).
- Реестр и фабрика позволяют подключать/отключать фичевые БД без изменения ядра. Каждая БД имеет своё имя/ключ, но создаётся одним механизмом.
- Использование SQLCipher на обоих платформах обеспечивает одинаковый уровень защиты; Room/DAO скрывает SQL на Android, а iOS опирается на общий SQL через драйвер.
