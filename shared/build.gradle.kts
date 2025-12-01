import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    // ✅ Создаём XCFramework
    val xcf = XCFramework()

    // ✅ Только iOS-таргеты
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        iosX64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true

            // ✅ экспорт всех фич-модулей
            export(project(":core"))
            export(project(":feature_chats"))
            export(project(":feature_menu"))
            export(project(":feature_profile"))
            export(project(":feature_products"))
            export(project(":feature_auth"))
            export(project(":network_service"))
            export(project(":data_service"))

            // ✅ новый способ задать минимальную iOS
            binaryOptions["iosDeploymentTarget"] = "17.0"

            // ✅ добавляем в XCFramework
            xcf.add(this)

            // ✅ Экспорт DI, Compose и др. необходимых зависимостей
            export(libs.koin.core)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            api(project(":feature_chats"))
            api(project(":feature_menu"))
            api(project(":feature_profile"))
            api(project(":feature_products"))
            api(project(":feature_auth"))
            api(project(":network_service"))
            api(project(":data_service"))

            // Compose MPP
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.coroutines.core)

            // DI
            api(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(compose.components.resources)

            // Lifecycle ViewModel (MPP от JetBrains, не Android-only)
            implementation(libs.androidx.lifecycle.viewmodel)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.accompanist)
        }

        iosMain.dependencies {
            api(libs.koin.core)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.gear.hub.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

// ✅ Таска для удобного билда XCFramework
tasks.register("packForXcode") {
    group = "build"
    description = "Builds an XCFramework for Xcode"

    dependsOn("assembleSharedReleaseXCFramework")

    doLast {
        println("✅ XCFramework собрано. Проверьте: shared/build/XCFrameworks/release/Shared.xcframework")
    }
}
