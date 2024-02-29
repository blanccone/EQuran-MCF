import extensions.api
import extensions.debugImplementation
import extensions.implementation
import java.util.Properties

plugins {
    id("commons.android-library")
}

android {

    val endpointFile = file("../endpoint.properties")
    val endpointProperties = Properties()
    endpointProperties.load(endpointFile.inputStream())

    defaultConfig {
        buildConfigField("String", "BASE_URL", "${endpointProperties["BASE_URL"]}")
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(Libs.CORE_KTX)
    api(Libs.LAYOUT)
    api(Libs.UI_LAYER)
    api(Libs.COROUTINES)
    api(Libs.RETROFIT)
    debugImplementation(Libs.CHUCKER)
}