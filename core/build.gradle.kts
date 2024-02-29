import extensions.api
import extensions.debugImplementation

plugins {
    id("commons.android-library")
}

dependencies {
    api(Libs.CORE_KTX)
    api(Libs.LAYOUT)
    api(Libs.UI_LAYER)
    api(Libs.COROUTINES)
    api(Libs.RETROFIT)
    debugImplementation(Libs.CHUCKER)
}