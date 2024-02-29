import extensions.api
import extensions.implementation
import extensions.kapt

plugins {
    id(BuildPlugins.ANDROID_APPLICATION)
    id(BuildPlugins.KOTLIN)
    id(BuildPlugins.KAPT)
}

android {
    namespace = BuildAndroidConfig.APP_ID
    compileSdk = BuildAndroidConfig.COMPILE_SDK_VERSION

    defaultConfig {
        applicationId = BuildAndroidConfig.APP_ID
        minSdk = BuildAndroidConfig.MIN_SDK_VERSION
        targetSdk = BuildAndroidConfig.TARGET_SDK_VERSION

        versionCode = BuildAndroidConfig.VERSION_CODE
        versionName = BuildAndroidConfig.VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(BuildModules.CORE))

    implementation(Libs.LIFECYCLE)
    implementation(Libs.DAGGER_HILT)

    kapt(Libs.DAGGER_HILT_COMPILER)
}