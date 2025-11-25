plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
}

dependencies {
    add("kspAndroid", libs.room.compiler)
}

kotlin {
    androidTarget()

    val xcfName = "feature_authKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
            linkerOpts("-lsqlite3")
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
            linkerOpts("-lsqlite3")
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
            linkerOpts("-lsqlite3")
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core"))
                implementation(project(":data_service"))
                implementation(project(":network_service"))

                implementation(libs.kotlin.stdlib)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.koin.core)
                implementation(libs.koin.compose)

                implementation(libs.androidx.lifecycle.viewmodel)
            }
        }

        commonTest { dependencies { implementation(libs.kotlin.test) } }

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

        val androidInstrumentedTest by maybeCreating

        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.runner)
            implementation(libs.androidx.core)
            implementation(libs.androidx.junit)
        }

        iosMain {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.sqliter)
            }
        }
    }
}

android {
    namespace = "com.gear.hub.auth"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

