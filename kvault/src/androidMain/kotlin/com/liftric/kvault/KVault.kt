package com.liftric.kvault

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

actual class KVault(private val context: Context) {
    private val encSharedPrefs: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        encSharedPrefs = EncryptedSharedPreferences.create(
            context,
            "secure-shared-preferences",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Saves a string value in the shared preferences.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: String): Boolean {
        return encSharedPrefs
            .edit()
            .putString(key, value)
            .commit()
    }

    /**
     * Saves an int value in the shared preferences.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: Int): Boolean {
        return encSharedPrefs
            .edit()
            .putInt(key, value)
            .commit()
    }

    /**
     * Saves a long value in the shared preferences.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: Long): Boolean {
        return encSharedPrefs
            .edit()
            .putLong(key, value)
            .commit()
    }

    /**
     * Saves a float value in the shared preferences.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: Float): Boolean {
        return encSharedPrefs
            .edit()
            .putFloat(key, value)
            .commit()
    }

    /**
     * Saves a double value in the shared preferences.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: Double): Boolean {
        return encSharedPrefs
            .edit()
            .putLong(key, value.toRawBits())
            .commit()
    }

    /**
     * Saves a boolean value in the shared preferences.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: Boolean): Boolean {
        return encSharedPrefs
            .edit()
            .putBoolean(key, value)
            .commit()
    }

    /**
     * Checks if object with key exists in the shared preferences.
     * @param forKey The key to query
     * @return True or false, depending on wether it is in the shared preferences or not
     */
    actual fun existsObject(forKey: String): Boolean {
        return encSharedPrefs.contains(forKey)
    }

    /**
     * Returns the string value of an object in the shared preferences.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun string(forKey: String): String? {
        return encSharedPrefs.getString(forKey, null)
    }

    /**
     * Returns the int value of an object in the shared preferences.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun int(forKey: String): Int? {
        if (existsObject(forKey)) {
            return encSharedPrefs.getInt(forKey, Int.MIN_VALUE)
        }
        return null
    }

    /**
     * Returns the long value of an object in the shared preferences.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun long(forKey: String): Long? {
        if (existsObject(forKey)) {
            return encSharedPrefs.getLong(forKey, Long.MIN_VALUE)
        }
        return null
    }

    /**
     * Returns the float value of an object in the shared preferences.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun float(forKey: String): Float? {
        if (existsObject(forKey)) {
            return encSharedPrefs.getFloat(forKey, Float.MIN_VALUE)
        }
        return null
    }

    /**
     * Returns the double value of an object in the shared preferences.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun double(forKey: String): Double? {
        if (existsObject(forKey)) {
            return Double.fromBits(encSharedPrefs.getLong(forKey, Double.MIN_VALUE.toRawBits()))
        }
        return null
    }

    /**
     * Returns the boolean value of an object in the shared preferences.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun bool(forKey: String): Boolean? {
        if (existsObject(forKey)) {
            return encSharedPrefs.getBoolean(forKey, false)
        }
        return null
    }

    /**
     * Deletes object with the given key from the shared preferences.
     * @param forKey The key to query
     */
    actual fun deleteObject(forKey: String): Boolean {
        return encSharedPrefs.edit().remove(forKey).commit()
    }

    /**
     * Deletes all objects from the SharedPreferences.
     */
    actual fun clear() {
        encSharedPrefs.edit().clear().apply()
    }
}
