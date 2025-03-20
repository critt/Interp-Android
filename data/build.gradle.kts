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
    testImplementation(kotlin("test"))

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

    //testing - JUnit
    //testImplementation(Dependencies.Testing.JUNIT)
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    //testing - Mockito
    testImplementation(Dependencies.Testing.MOCKITO_CORE)
    testImplementation(Dependencies.Testing.MOCKITO_INLINE)
    testImplementation(Dependencies.Testing.MOCKITO_KOTLIN)
    //testing - Truth
    testImplementation(Dependencies.Testing.TRUTH)
    //testing - support
    testImplementation(Dependencies.Testing.COROUTINES_TEST)
    testImplementation(Dependencies.Testing.ANDROIDX_TEST_CORE)
    testImplementation(Dependencies.Testing.ANDROIDX_TEST_RULES)
    testImplementation(Dependencies.Testing.ANDROIDX_TEST_RUNNER)
    testImplementation(Dependencies.Testing.ANDROIDX_TEST_EXT_JUNIT)
    testImplementation(Dependencies.Testing.ANDROIDX_TEST_EXT_JUNIT_KTX)
}



tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {     // This is for logging and can be removed.
        events("passed", "skipped", "failed")
    }
}
