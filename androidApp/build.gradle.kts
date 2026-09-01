import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    implementation(project.dependencies.platform(libs.firebase.bom))
    implementation(libs.firebase.appcheck)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.auth)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.firebase.ads)
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.foundation)
    implementation(projects.shared)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.compose.navigation)
    implementation(libs.firebase.appcheck.debug)
    debugImplementation(libs.compose.uiTooling)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}

android {
    namespace = "es.myvacations.myvacations"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "es.myvacations.myvacations"
        minSdk = libs.versions.android.minSdk.get().toInt()
        versionCode = (LocalDate.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "1")
            .toInt()
        versionName = "1.0.0"
        multiDexEnabled = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("debug") {
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        buildConfig = true
    }

    flavorDimensions += listOf("enviroment")
    productFlavors {
        create("dev") {
            dimension = "enviroment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
        create("pro") {
            dimension = "enviroment"
        }
    }

}

tasks.register("r8Version") {
    description = "r8"
    group = "r8"
    doLast {
        println("R8_VERSION_START:" + com.android.tools.r8.Version.getVersionString() + ":R8_VERSION_END")
    }
}