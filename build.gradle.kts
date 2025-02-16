// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version Dependencies.PluginVersions.ANDROID_GRADLE apply false
    id("com.android.library") version Dependencies.PluginVersions.ANDROID_GRADLE apply false
    id("org.jetbrains.kotlin.android") version Dependencies.PluginVersions.KOTLIN apply false
    kotlin("plugin.serialization") version Dependencies.PluginVersions.SERIALIZATION apply false
    id("com.google.dagger.hilt.android") version Dependencies.PluginVersions.HILT apply false
    id("com.google.gms.google-services") version Dependencies.PluginVersions.GOOGLE_SERVICES apply false
}