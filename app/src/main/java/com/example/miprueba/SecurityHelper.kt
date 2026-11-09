package com.example.miprueba

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * SecurityHelper proporciona almacenamiento seguro para credenciales de usuario
 * y parámetros de configuración IoT conforme al estándar de seguridad ISO 27400.
 *
 * Utiliza EncryptedSharedPreferences respaldado por la clave maestra AES-256 de Android Keystore.
 */
class SecurityHelper(context: Context) {

    // Se fuerza el uso del Application Context para prevenir memory leaks si se pasa un Activity context
    private val appContext: Context = context.applicationContext

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val sharedPreferences by lazy {
        try {
            EncryptedSharedPreferences.create(
                appContext,
                PREFS_FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error crítico al inicializar EncryptedSharedPreferences (ISO 27400)", e)
            // En caso de recuperación ante corrupción de Keystore, resetear archivo de preferencias
            appContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE).edit().clear().apply()
            EncryptedSharedPreferences.create(
                appContext,
                PREFS_FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    /**
     * Guarda de forma cifrada (AES-256) el nombre de usuario.
     */
    fun saveUsername(username: String): Boolean {
        return try {
            sharedPreferences.edit()
                .putString(KEY_USERNAME, username.trim())
                .apply()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error guardando usuario cifrado", e)
            false
        }
    }

    /**
     * Obtiene el nombre de usuario cifrado guardado previamente.
     */
    fun getUsername(): String {
        return try {
            sharedPreferences.getString(KEY_USERNAME, "") ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error leyendo usuario cifrado", e)
            ""
        }
    }

    /**
     * Verifica si existe un usuario guardado.
     */
    fun isUserSaved(): Boolean = getUsername().isNotBlank()

    /**
     * Elimina de forma segura las credenciales almacenadas.
     */
    fun clearCredentials(): Boolean {
        return try {
            sharedPreferences.edit().clear().apply()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error limpiando credenciales cifradas", e)
            false
        }
    }

    companion object {
        private const val TAG = "SecurityHelper"
        private const val PREFS_FILE_NAME = "encrypted_shared_prefs_iot"
        private const val KEY_USERNAME = "encrypted_username"
    }
}
