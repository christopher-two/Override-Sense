import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.google.devtools.ksp)
}
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}
android {
    namespace = "org.override.sense"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "org.override.sense"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
        }

        create("benchmark") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // Baseline Profile & Performance Optimizations
    experimentalProperties["android.experimental.art-profile-r8-rewriting"] = true
    experimentalProperties["android.experimental.r8.dex-startup-optimization"] = true
}

kotlin {
    target {
        compilerOptions {
            optIn.add("kotlin.RequiresOptIn")
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material3.android)

    // Utils
    implementation(libs.pdfbox.android)
    implementation(libs.timber)
    implementation(libs.androidx.work.runtime.ktx)

    //Nav 3
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)

    // TensorFlowLite
    implementation(libs.tensorflow.lite.support) {
        exclude(group = "org.tensorflow", module = "tensorflow-lite-support-api")
        exclude(group = "com.google.ai.edge.litert", module = "litert-support-api")
    }
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.tensorflow.lite) {
        exclude(group = "org.tensorflow", module = "tensorflow-lite-api")
    }

    // UI & Design System
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.material.kolor)
    implementation(libs.material.icons.ext)
    implementation(libs.core.splashscreen)
    implementation(libs.qrose)
    implementation(libs.accompanist.permissions)

    // Dependency Injection
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.navigation3)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.androidx.workmanager)

    // Data, Network & Auth
    implementation(libs.datastore.pref)
    implementation(libs.datastore.pref.core)

    // Core & Kotlin X
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)

    // Test & Tools
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}