object Dependencies {
    object PluginVersions {
        const val KOTLIN = "2.0.0"
        const val HILT = "2.51.1"
        const val ANDROID_GRADLE = "8.7.3"
        const val GOOGLE_SERVICES = "4.4.2"
        const val SERIALIZATION = "1.9.22"
    }

    object Retrofit {
        const val RETROFIT = "com.squareup.retrofit2:retrofit:2.9.0"
        const val CONVERTER_GSON = "com.squareup.retrofit2:converter-gson:2.9.0"
        const val COROUTINES_ADAPTER = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"
    }

    object Timber {
        const val TIMBER = "com.jakewharton.timber:timber:5.0.1"
    }

    object Firebase {
        const val BOM = "com.google.firebase:firebase-bom:33.8.0"
        const val AUTH = "com.google.firebase:firebase-auth-ktx"
    }

    object FirebaseUI {
        const val FIREBASE_UI = "com.firebaseui:firebase-ui-auth:8.0.0"
    }

    object Hilt {
        const val COMPILER = "com.google.dagger:hilt-compiler:${PluginVersions.HILT}"
        const val ANDROID_COMPILER = "com.google.dagger:hilt-android-compiler:${PluginVersions.HILT}"
        const val ANDROID = "com.google.dagger:hilt-android:${PluginVersions.HILT}"
    }

    object Compose {
        const val BOM = "androidx.compose:compose-bom:2024.10.01"
        const val MATERIAL3 = "androidx.compose.material3:material3"
        const val UI_TOOLING_PREVIEW = "androidx.compose.ui:ui-tooling-preview"
        const val UI_TOOLING = "androidx.compose.ui:ui-tooling"
        const val ACTIVITY_COMPOSE = "androidx.activity:activity-compose:1.10.0"
        const val LIFECYCLE_VIEWMODEL_COMPOSE = "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7"
    }

    object Material {
        const val MATERIAL = "com.google.android.material:material:1.12.0"
    }

    object Testing {
        // JUnit
        private const val JUNIT_VERSION = "4.13.2"
        const val JUNIT = "junit:junit:${JUNIT_VERSION}"

        // Mockito
        private const val MOCKITO_VERSION = "5.2.0"
        private const val MOCKITO_KOTLIN_VERSION = "5.1.0"
        const val MOCKITO_CORE = "org.mockito:mockito-core:${MOCKITO_VERSION}"
        const val MOCKITO_INLINE = "org.mockito:mockito-inline:${MOCKITO_VERSION}"
        const val MOCKITO_KOTLIN = "org.mockito.kotlin:mockito-kotlin:${MOCKITO_KOTLIN_VERSION}"

        // Truth
        private const val TRUTH_VERSION = "1.1.5"
        const val TRUTH = "com.google.truth:truth:${TRUTH_VERSION}"

        // Coroutines test support
        private const val COROUTINES_TEST_VERSION = "1.7.3"
        const val COROUTINES_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${COROUTINES_TEST_VERSION}"

        // AndroidX Test library for testing Android components
        private const val ANDROIDX_TEST_VERSION = "1.5.0"
        private const val ANDROIDX_TEST_EXT_VERSION = "1.1.5"

        const val ANDROIDX_TEST_CORE = "androidx.test:core:${ANDROIDX_TEST_VERSION}"
        const val ANDROIDX_TEST_RULES = "androidx.test:rules:${ANDROIDX_TEST_VERSION}"
        const val ANDROIDX_TEST_EXT_JUNIT = "androidx.test.ext:junit:${ANDROIDX_TEST_EXT_VERSION}"
        const val ANDROIDX_TEST_EXT_JUNIT_KTX = "androidx.test.ext:junit-ktx:${ANDROIDX_TEST_EXT_VERSION}"
        const val ANDROIDX_TEST_RUNNER = "androidx.test:runner:${ANDROIDX_TEST_VERSION}"
    }

    object Crypto {
        const val CRYPTO_KTX = "androidx.security:security-crypto:1.1.0-alpha06"
    }

    object SocketIO {
        const val SOCKET_IO = "io.socket:socket.io-client:2.1.1"
    }

    object Serialization {
        const val SERIALIZATION = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0"
    }
}