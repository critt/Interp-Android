package com.critt.trandroidlator.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.critt.trandroidlator.util.Constants.PREFS_KEY_FB_TOKEN
import com.critt.trandroidlator.util.Constants.PREFS_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    var token: String? = null

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAuthToken(token: String) {
        sharedPreferences.edit().putString(PREFS_KEY_FB_TOKEN, token).apply()
        this.token = token
    }

    fun clearAuthToken() {
        token = null
        sharedPreferences.edit().remove(PREFS_KEY_FB_TOKEN).apply()
    }
}