package com.hellboy.tehrannavai.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class ApiKeyManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secret_keys_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveGeminiKey(key: String) {
        sharedPreferences.edit().putString("gemini_key", key).apply()
    }

    fun getGeminiKey(): String? {
        return sharedPreferences.getString("gemini_key", null)
    }

    fun saveMapsKey(key: String) {
        sharedPreferences.edit().putString("maps_key", key).apply()
    }

    fun getMapsKey(): String? {
        return sharedPreferences.getString("maps_key", null)
    }
}
