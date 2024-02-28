// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version Versions.ANDROID_GRADLE apply false
    id("org.jetbrains.kotlin.android") version Versions.KOTLIN apply false
    id("com.google.devtools.ksp") version Versions.KSP apply false
    id("com.google.dagger.hilt.android") version Versions.DAGGER_HILT apply false
}