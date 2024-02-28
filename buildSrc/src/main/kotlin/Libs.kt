object Libs {
    const val CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"

    val COROUTINES = listOf(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES}",
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}"
    )

    const val DAGGER_HILT = "com.google.dagger:hilt-android:${Versions.DAGGER_HILT}"
    const val DAGGER_HILT_COMPILER = "com.google.dagger:hilt-android-compiler:${Versions.DAGGER_HILT}"

    val RETROFIT = listOf(
        "com.squareup.retrofit2:retrofit:${Versions.RETROFIT}",
        "com.squareup.retrofit2:converter-gson:${Versions.RETROFIT}",
        "com.squareup.okhttp3:logging-interceptor:${Versions.OKHTPP}"
    )

    const val CHUCKER = "com.github.chuckerteam.chucker:library:${Versions.CHUCKER}"

    const val PAGING = "androidx.paging:paging-runtime-ktx:${Versions.PAGING}"

    val ROOM = listOf(
        "androidx.room:room-ktx:${Versions.ROOM}",
        "androidx.room:room-runtime:${Versions.ROOM}"
    )
    const val ROOM_COMPILER = "androidx.room:room-compiler:${Versions.ROOM}"

    val LIFECYCLE = listOf(
        "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFECYCLE}",
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFECYCLE}",
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE}"
    )

    val UI_LAYER = listOf(
        "androidx.activity:activity:${Versions.ACTIVITY_KTX}",
        "androidx.fragment:fragment:${Versions.FRAGMENT_KTX}"
    )

    val LAYOUT = listOf(
        "androidx.appcompat:appcompat:${Versions.APPCOMPAT}",
        "com.google.android.material:material:${Versions.MATERIAL}"
    )
}