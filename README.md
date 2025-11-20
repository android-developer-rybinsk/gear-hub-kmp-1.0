# GearHubKMP

# Сборка iOS проекта
    Для сборки iOS проекна необходимо выполнить:
    1. ./gradlew shared:packForXcode --no-daemon --info
    2. Скопировать сформировавшуюся папку "shared.xcframework" путь .../GearHubKMP/shared/build/XCFrameworks/release/shared.xcframework
    3. Добавить эту папку в Xcode в проекте iosApp в раздел FrameWorks
