import extensions.implementation
import extensions.kapt
import java.util.Properties

plugins {
    id(BuildPlugins.ANDROID_APPLICATION)
    id(BuildPlugins.KOTLIN)
    id(BuildPlugins.KAPT)
}

android {
    namespace = BuildAndroidConfig.APP_ID
    compileSdk = BuildAndroidConfig.COMPILE_SDK_VERSION

    val endpointFile = file("../endpoint.properties")
    val endpointProperties = Properties()
    endpointProperties.load(endpointFile.inputStream())

    defaultConfig {
        applicationId = BuildAndroidConfig.APP_ID
        minSdk = BuildAndroidConfig.MIN_SDK_VERSION
        targetSdk = BuildAndroidConfig.TARGET_SDK_VERSION

        versionCode = BuildAndroidConfig.VERSION_CODE
        versionName = BuildAndroidConfig.VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(Libs.CORE_KTX)
    implementation(Libs.DAGGER_HILT)

    kapt(Libs.DAGGER_HILT_COMPILER)
}