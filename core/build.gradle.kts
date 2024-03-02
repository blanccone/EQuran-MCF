import extensions.api
import extensions.debugImplementation
import java.util.Properties

plugins {
    id("commons.android-library")
}

android {
    namespace = BuildAndroidConfig.CORE_ID

    val endpointFile = file("../endpoint.properties")
    val endpointProperties = Properties()
    endpointProperties.load(endpointFile.inputStream())

    defaultConfig {
        buildConfigField("String", "BASE_URL", "${endpointProperties["BASE_URL"]}")
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    api(Libs.CORE_KTX)
    api(Libs.LAYOUT)
    api(Libs.UI_LAYER)
    api(Libs.COROUTINES)
    api(Libs.RETROFIT)
    debugImplementation(Libs.CHUCKER_DEBUG)
    releaseImplementation(Libs.CHUCKER_RELEASE)
}