plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
}

kotlin {
    androidLibrary {
        namespace = "com.gear.hub.data"
        compileSdk = 36
        defaultConfig {
            minSdk = 24
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.stdlib)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation(libs.room.runtime)
            implementation(libs.room.ktx)
            implementation(libs.sqlcipher)
        }

        iosMain.dependencies {
            implementation(libs.koin.core)
        }
    }
}
