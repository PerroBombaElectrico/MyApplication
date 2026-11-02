package com.example.miprueba

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * SecurityHelper provides secure storage for user credentials and preferences
 * using EncryptedSharedPreferences backed by Android Keystore MasterKey.
 */
class SecurityHelper(private val context: Context) {

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val sharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveUsername(username: String) {
        try {
            sharedPreferences.edit()
                .putString(KEY_USERNAME, username)
                .apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving username", e)
        }
    }

    fun getUsername(): String {
        return try {
            sharedPreferences.getString(KEY_USERNAME, "") ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error reading username", e)
            ""
        }
    }

    companion object {
        private const val TAG = "SecurityHelper"
        private const val PREFS_FILE_NAME = "encrypted_shared_prefs"
        private const val KEY_USERNAME = "username"
    }
}
