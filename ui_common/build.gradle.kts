plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") version Dependencies.PluginVersions.KOTLIN
}

android {
    namespace = "com.critt.ui_common"
    compileSdk = BuildConfiguration.COMPILE_SDK

    defaultConfig {
        minSdk = BuildConfiguration.MIN_SDK
        consumerProguardFiles("consumer-rules.pro")
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

dependencies {
    //material theme
    implementation(Dependencies.Material.MATERIAL)

    //compose
    implementation(platform(Dependencies.Compose.BOM))
    androidTestImplementation(platform(Dependencies.Compose.BOM))
    implementation(Dependencies.Compose.MATERIAL3)

    // Android Studio Preview support
    debugImplementation(Dependencies.Compose.UI_TOOLING_PREVIEW)
    debugImplementation(Dependencies.Compose.UI_TOOLING)

    //testing
    testImplementation(Dependencies.Testing.JUNIT)
    testImplementation(Dependencies.Testing.JUNIT_JUPITER)
}