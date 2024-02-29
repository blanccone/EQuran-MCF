package commons

import BuildAndroidConfig
import extensions.implementation
import extensions.kapt
import Libs
import java.util.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = BuildAndroidConfig.APP_ID
    compileSdk = BuildAndroidConfig.COMPILE_SDK_VERSION

    val endpointFile = file("../endpoint.properties")
    val endpointProperties = Properties()
    endpointProperties.load(endpointFile.inputStream())

    defaultConfig {
        minSdk = BuildAndroidConfig.MIN_SDK_VERSION
        buildConfigField("String", "BASE_URL", "${endpointProperties["BASE_URL"]}")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation(Libs.DAGGER_HILT)
    kapt(Libs.DAGGER_HILT_COMPILER)
}