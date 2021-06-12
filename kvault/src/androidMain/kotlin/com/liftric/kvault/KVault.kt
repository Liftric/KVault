package com.liftric.kvault

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

actual open class KVault(private val context: Context) {
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
     * Saves a string value in the SharedPreferences.
     * @param key The key to store
     * @param stringValue The value to store
     */
    actual fun set(key: String, stringValue: String): Boolean {
        return encSharedPrefs
            .edit()
            .putString(key, stringValue)
            .commit()
    }

    /**
     * Saves an int value in the SharedPreferences.
     * @param key The key to store
     * @param intValue The value to store
     */
    actual fun set(key: String, intValue: Int): Boolean {
        return encSharedPrefs
            .edit()
            .putInt(key, intValue)
            .commit()
    }

    /**
     * Saves a long value in the SharedPreferences.
     * @param key The key to store
     * @param longValue The value to store
     */
    actual fun set(key: String, longValue: Long): Boolean {
        return encSharedPrefs
            .edit()
            .putLong(key, longValue)
            .commit()
    }

    /**
     * Saves a float value in the SharedPreferences.
     * @param key The key to store
     * @param floatValue The value to store
     */
    actual fun set(key: String, floatValue: Float): Boolean {
        return encSharedPrefs
            .edit()
            .putFloat(key, floatValue)
            .commit()
    }

    /**
     * Saves a double value in the SharedPreferences.
     * @param key The key to store
     * @param doubleValue The value to store
     */
    actual fun set(key: String, doubleValue: Double): Boolean {
        return encSharedPrefs
            .edit()
            .putLong(key, doubleValue.toRawBits())
            .commit()
    }

    /**
     * Saves a boolean value in the SharedPreferences.
     * @param key The key to store
     * @param boolValue The value to store
     */
    actual fun set(key: String, boolValue: Boolean): Boolean {
        return encSharedPrefs
            .edit()
            .putBoolean(key, boolValue)
            .commit()
    }

    /**
     * Checks if object with key exists in the SharedPreferences.
     * @param forKey The key to query
     * @return True or false, depending on wether it is in the shared preferences or not
     */
    actual fun existsObject(forKey: String): Boolean {
        return encSharedPrefs.contains(forKey)
    }

    /**
     * Returns the string value of an object in the SharedPreferences.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun string(forKey: String): String? {
        return encSharedPrefs.getString(forKey, null)
    }

    /**
     * Returns the int value of an object in the SharedPreferences.
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
     * Returns the long value of an object in the SharedPreferences.
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
     * Returns the float value of an object in the SharedPreferences.
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
     * Returns the double value of an object in the SharedPreferences.
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
     * Returns the boolean value of an object in the SharedPreferences.
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
     * Deletes object with the given key from the SharedPreferences.
     * @param forKey The key to query
     */
    actual fun deleteObject(forKey: String): Boolean {
        return encSharedPrefs.edit().remove(forKey).commit()
    }

    /**
     * Deletes all objects from the SharedPreferences.
     */
    actual fun clear(): Boolean {
        return encSharedPrefs.edit().clear().commit()
    }
}
