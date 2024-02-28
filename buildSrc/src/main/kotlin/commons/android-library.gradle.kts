package commons

import BuildAndroidConfig
import extensions.implementation
import extensions.kapt
import Libs

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = BuildAndroidConfig.APP_ID
    compileSdk = BuildAndroidConfig.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = BuildAndroidConfig.MIN_SDK_VERSION
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(Libs.CORE_KTX)
    implementation(Libs.COROUTINES)
    implementation(Libs.DAGGER_HILT)

    kapt(Libs.DAGGER_HILT_COMPILER)
}