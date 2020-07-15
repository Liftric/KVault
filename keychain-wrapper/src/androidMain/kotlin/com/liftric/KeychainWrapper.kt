package com.liftric

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

actual class KeychainWrapper(private val context: Context) {
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
     * Saves a string value in the keychain.
     * @param value The value to store
     * @param forKey The key to query
     */
    actual fun set(value: String, forKey: String): Boolean {
        return encSharedPrefs
            .edit()
            .putString(forKey, value)
            .commit()
    }

    /**
     * Saves an int value in the keychain.
     * @param value The value to store
     * @param forKey The key to query
     */
    actual fun set(value: Int, forKey: String): Boolean {
        return encSharedPrefs
            .edit()
            .putInt(forKey, value)
            .commit()
    }

    /**
     * Saves a long value in the keychain.
     * @param value The value to store
     * @param forKey The key to query
     */
    actual fun set(value: Long, forKey: String): Boolean {
        return encSharedPrefs
            .edit()
            .putLong(forKey, value)
            .commit()
    }

    /**
     * Saves a float value in the keychain.
     * @param value The value to store
     * @param forKey The key to query
     */
    actual fun set(value: Float, forKey: String): Boolean {
        return encSharedPrefs
            .edit()
            .putFloat(forKey, value)
            .commit()
    }

    /**
     * Saves a double value in the keychain.
     * @param value The value to store
     * @param forKey The key to query
     */
    actual fun set(value: Double, forKey: String): Boolean {
        return encSharedPrefs
            .edit()
            .putFloat(forKey, value.toFloat())
            .commit()
    }

    /**
     * Saves a boolean value in the keychain.
     * @param value The value to store
     * @param forKey The key to query
     */
    actual fun set(value: Boolean, forKey: String): Boolean {
        return encSharedPrefs
            .edit()
            .putBoolean(forKey, value)
            .commit()
    }

    /**
     * Checks if object with key exists in the keychain.
     * @param forKey The key to query
     * @return True or false, depending on wether it is in the keychain or not
     */
    actual fun existsObject(forKey: String): Boolean {
       return encSharedPrefs.contains(forKey)
    }

    /**
     * Returns the string value of an object in the keychain.
     * @param forKey The key to query
     * @return The stored string value
     */
    actual fun string(forKey: String): String? {
        return encSharedPrefs.getString(forKey, null)
    }

    /**
     * Returns the int value of an object in the keychain.
     * @param forKey The key to query
     * @return The stored int value
     */
    actual fun int(forKey: String): Int? {
        if (existsObject(forKey)) {
            return encSharedPrefs.getInt(forKey, Int.MIN_VALUE)
        }
        return null
    }

    /**
     * Returns the long value of an object in the keychain.
     * @param forKey The key to query
     * @return The stored long value
     */
    actual fun long(forKey: String): Long? {
        if (existsObject(forKey)) {
            return encSharedPrefs.getLong(forKey, Long.MIN_VALUE)
        }
        return null
    }

    /**
     * Returns the int value of an object in the keychain.
     * @param forKey The key to query
     * @return The stored double lue
     */
    actual fun double(forKey: String): Double? {
        if (existsObject(forKey)) {
            return encSharedPrefs.getFloat(forKey, Float.MIN_VALUE).toDouble()
        }
        return null
    }

    /**
     * Returns the int value of an object in the keychain.
     * @param forKey The key to query
     * @return The stored float value
     */
    actual fun float(forKey: String): Float? {
        if (existsObject(forKey)) {
            return encSharedPrefs.getFloat(forKey, Float.MIN_VALUE)
        }
        return null
    }

    /**
     * Returns the boolean value of an object in the keychain.
     * @param forKey The key to query
     * @return The stored boolean value
     */
    actual fun bool(forKey: String): Boolean? {
        if (existsObject(forKey)) {
            return encSharedPrefs.getBoolean(forKey, false)
        }
        return null
    }

    /**
     * Deletes object with the given key from the keychain.
     * @param forKey The key to query
     */
    actual fun deleteObject(forKey: String): Boolean {
        return encSharedPrefs.edit().remove(forKey).commit()
    }

    /**
     * Deletes all objects with the service name from the keychain.
     */
    actual fun clear() {
        encSharedPrefs.edit().clear().apply()
    }
}
