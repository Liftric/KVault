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
        TODO("Not yet implemented")
    }

    /**
     * Saves an int value in the keychain.
     * @param value The value to store
     * @param forKey The key to query
     */
    actual fun set(value: Int, forKey: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Saves a long value in the keychain.
     * @param value The value to store
     * @param forKey The key to query
     */
    actual fun set(value: Long, forKey: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Saves a float value in the keychain.
     * @param value The value to store
     * @param forKey The key to query
     */
    actual fun set(value: Float, forKey: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Saves a double value in the keychain.
     * @param value The value to store
     * @param forKey The key to query
     */
    actual fun set(value: Double, forKey: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Saves a boolean value in the keychain.
     * @param value The value to store
     * @param forKey The key to query
     */
    actual fun set(value: Boolean, forKey: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Checks if object with key exists in the keychain.
     * @param forKey The key to query
     * @return True or false, depending on wether it is in the keychain or not
     */
    actual fun existsObject(forKey: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Returns the string value of an object in the keychain.
     * @param forKey The key to query
     * @return The stored string value
     */
    actual fun string(forKey: String): String? {
        TODO("Not yet implemented")
    }

    /**
     * Returns the int value of an object in the keychain.
     * @param forKey The key to query
     * @return The stored int value
     */
    actual fun int(forKey: String): Int? {
        TODO("Not yet implemented")
    }

    /**
     * Returns the long value of an object in the keychain.
     * @param forKey The key to query
     * @return The stored long value
     */
    actual fun long(forKey: String): Long? {
        TODO("Not yet implemented")
    }

    /**
     * Returns the int value of an object in the keychain.
     * @param forKey The key to query
     * @return The stored double lue
     */
    actual fun double(forKey: String): Double? {
        TODO("Not yet implemented")
    }

    /**
     * Returns the int value of an object in the keychain.
     * @param forKey The key to query
     * @return The stored float value
     */
    actual fun float(forKey: String): Float? {
        TODO("Not yet implemented")
    }

    /**
     * Returns the boolean value of an object in the keychain.
     * @param forKey The key to query
     * @return The stored boolean value
     */
    actual fun bool(forKey: String): Boolean? {
        TODO("Not yet implemented")
    }

    /**
     * Deletes object with the given key from the keychain.
     * @param forKey The key to query
     */
    actual fun deleteObject(forKey: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Deletes all objects with the service name from the keychain.
     */
    actual fun clear() {
    }

}
