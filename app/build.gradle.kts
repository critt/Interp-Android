plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose") version Dependencies.PluginVersions.KOTLIN
    kotlin("plugin.serialization") version Dependencies.PluginVersions.SERIALIZATION
}

android {
    namespace = "com.critt.interp"
    compileSdk = BuildConfiguration.COMPILE_SDK

    defaultConfig {
        applicationId = "com.critt.interp"
        minSdk = BuildConfiguration.MIN_SDK
        targetSdk = BuildConfiguration.TARGET_SDK
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = BuildConfiguration.SOURCE_COMPATIBILITY
        targetCompatibility = BuildConfiguration.TARGET_COMPATIBILITY
    }
    kotlinOptions {
        jvmTarget = BuildConfiguration.JVM_TARGET
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

dependencies {
    //TODO: Dependency management across modules
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(project(":ui_common"))

    //firebase
    implementation(Dependencies.Firebase.AUTH)
    implementation(platform(Dependencies.Firebase.BOM))

    //firebase UI
    implementation(Dependencies.FirebaseUI.FIREBASE_UI)

    //timber
    implementation(Dependencies.Timber.TIMBER)

    //hilt
    kapt(Dependencies.Hilt.COMPILER)
    kapt(Dependencies.Hilt.ANDROID_COMPILER)
    implementation(Dependencies.Hilt.ANDROID)

    //lifecycle (currently using this for Flow.asLiveData())
    implementation(Dependencies.Lifecycle.LIVEDATA)

    //material theme
    implementation(Dependencies.Material.MATERIAL)

    //compose
    implementation(platform(Dependencies.Compose.BOM))
    androidTestImplementation(platform(Dependencies.Compose.BOM))
    implementation(Dependencies.Compose.MATERIAL3)

    // Android Studio Preview support
    debugImplementation(Dependencies.Compose.UI_TOOLING_PREVIEW)
    debugImplementation(Dependencies.Compose.UI_TOOLING)
    // UI Tests
    androidTestImplementation(Dependencies.Compose.UI_TEST_JUNIT4)
    debugImplementation(Dependencies.Compose.UI_TEST_MANIFEST)
    // Optional - Integration with LiveData
    implementation(Dependencies.Compose.LIVEDATA)
    // Optional - Integration with activities
    implementation(Dependencies.Compose.ACTIVITY_COMPOSE)
    // Optional - Integration with ViewModels
    implementation(Dependencies.Compose.LIFECYCLE_VIEWMODEL_COMPOSE)

    //testing
    testImplementation(Dependencies.Testing.JUNIT)
    testImplementation(Dependencies.Testing.JUNIT_JUPITER)
    androidTestImplementation(Dependencies.Testing.ANDROIDX_TEST_EXT)
    androidTestImplementation(Dependencies.Testing.ANDROIDX_TEST_ESPRESSO)
}