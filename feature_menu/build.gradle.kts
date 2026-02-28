plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget()

    val xcfName = "feature_menuKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core"))
                implementation(project(":data_service"))
                implementation(project(":network_service"))
                implementation(project(":feature_products"))

                implementation(libs.kotlin.stdlib)
                // Add KMP dependencies here
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.materialIconsExtended)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)

                // DI
                implementation(libs.koin.core)
                implementation(libs.koin.compose)

                implementation(compose.components.resources)

                // Lifecycle ViewModel (MPP от JetBrains, не Android-only)
                implementation(libs.androidx.lifecycle.viewmodel)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)
                implementation(libs.accompanist)
                implementation(libs.retrofit.core)
                implementation(libs.retrofit.kotlinx.serialization.converter)
                implementation(libs.okhttp.logging)
                implementation(libs.sqlcipher)
                implementation(libs.room.runtime)
                implementation(libs.room.ktx)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.koin.core)
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
}

android {
    namespace = "gearhub.feature.menu_feature"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    sourceSets["main"].res.srcDirs("src/androidMain/res")
}
