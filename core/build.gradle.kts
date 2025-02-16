plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.critt.core"
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
}

dependencies {
    //testing
    testImplementation(Dependencies.Testing.JUNIT)
    testImplementation(Dependencies.Testing.JUNIT_JUPITER)
}