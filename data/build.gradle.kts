import java.io.FileInputStream
import java.util.Properties

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("paths.properties")))
}

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.critt.data"
    compileSdk = BuildConfiguration.COMPILE_SDK

    defaultConfig {
        minSdk = BuildConfiguration.MIN_SDK
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "API_BASE_URL", "\"${properties["servicePath"] as String}\"")
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
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))

    implementation(Dependencies.SocketIO.SOCKET_IO) {
        exclude(group = "org.json", module = "json")
    }

    //shared prefs / crypto
    implementation(Dependencies.Crypto.CRYPTO_KTX)

    //timber
    implementation(Dependencies.Timber.TIMBER)

    //retrofit
    implementation(Dependencies.Retrofit.RETROFIT)
    implementation(Dependencies.Retrofit.CONVERTER_GSON)
    implementation(Dependencies.Retrofit.COROUTINES_ADAPTER)

    //hilt
    kapt(Dependencies.Hilt.COMPILER)
    kapt(Dependencies.Hilt.ANDROID_COMPILER)
    implementation(Dependencies.Hilt.ANDROID)

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    //testing
    testImplementation(Dependencies.Testing.JUNIT)
    testImplementation(Dependencies.Testing.JUNIT_JUPITER)
}