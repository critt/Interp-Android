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
        const val LIVEDATA = "androidx.compose.runtime:runtime-livedata"
        const val UI_TOOLING_PREVIEW = "androidx.compose.ui:ui-tooling-preview"
        const val UI_TOOLING = "androidx.compose.ui:ui-tooling"
        const val UI_TEST_JUNIT4 = "androidx.compose.ui:ui-test-junit4"
        const val UI_TEST_MANIFEST = "androidx.compose.ui:ui-test-manifest"

        const val ACTIVITY_COMPOSE = "androidx.activity:activity-compose:1.10.0"
        const val LIFECYCLE_VIEWMODEL_COMPOSE = "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7"
    }
    object Material {
        const val MATERIAL = "com.google.android.material:material:1.12.0"
    }
    object Lifecycle {
        //(currently using this for Flow.asLiveData())
        const val LIVEDATA = "androidx.lifecycle:lifecycle-livedata-ktx:2.8.7"
    }
    object Testing {
        const val JUNIT = "junit:junit:4.13.2"
        const val JUNIT_JUPITER = "org.junit.jupiter:junit-jupiter:5.8.1"
        const val ANDROIDX_TEST_EXT = "androidx.test.ext:junit:1.2.1"
        const val ANDROIDX_TEST_ESPRESSO = "androidx.test.espresso:espresso-core:3.6.1"
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