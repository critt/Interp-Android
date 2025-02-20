package com.critt.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.critt.core.Constants.PREFS_KEY_FB_TOKEN
import com.critt.core.Constants.PREFS_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(), // TODO: Exclude key from backup with exclusion rules
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun setAuthToken(token: String) {
        sharedPreferences.edit().putString(PREFS_KEY_FB_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(PREFS_KEY_FB_TOKEN, null)
    }

    fun clearAuthToken() {
        sharedPreferences.edit().remove(PREFS_KEY_FB_TOKEN).apply()
    }
}