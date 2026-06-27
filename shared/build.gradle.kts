import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    android {
        namespace = "es.myvacations.myvacations.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
            withDeviceTest {}
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.koin.android)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.icons)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.datetime)
            implementation(libs.napier)
            implementation(libs.calendar)
            implementation(libs.koalaplot)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }

    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

sqldelight {
    databases {
        create("MyVacationsDatabase") {
            packageName.set("es.myvacations.myvacations.data.database")
        }
    }
}