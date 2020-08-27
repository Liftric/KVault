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

    actual fun set(key: String, value: String): Boolean {
        return encSharedPrefs
            .edit()
            .putString(key, value)
            .commit()
    }

    actual fun set(key: String, value: Int): Boolean {
        return encSharedPrefs
            .edit()
            .putInt(key, value)
            .commit()
    }

    actual fun set(key: String, value: Long): Boolean {
        return encSharedPrefs
            .edit()
            .putLong(key, value)
            .commit()
    }

    actual fun set(key: String, value: Float): Boolean {
        return encSharedPrefs
            .edit()
            .putFloat(key, value)
            .commit()
    }

    actual fun set(key: String, value: Double): Boolean {
        return encSharedPrefs
            .edit()
            .putLong(key, value.toRawBits())
            .commit()
    }

    actual fun set(key: String, value: Boolean): Boolean {
        return encSharedPrefs
            .edit()
            .putBoolean(key, value)
            .commit()
    }

    actual fun existsObject(forKey: String): Boolean {
        return encSharedPrefs.contains(forKey)
    }

    actual fun string(forKey: String): String? {
        return encSharedPrefs.getString(forKey, null)
    }

    actual fun int(forKey: String): Int? {
        if (existsObject(forKey)) {
            return encSharedPrefs.getInt(forKey, Int.MIN_VALUE)
        }
        return null
    }

    actual fun long(forKey: String): Long? {
        if (existsObject(forKey)) {
            return encSharedPrefs.getLong(forKey, Long.MIN_VALUE)
        }
        return null
    }

    actual fun double(forKey: String): Double? {
        if (existsObject(forKey)) {
            return Double.fromBits(encSharedPrefs.getLong(forKey, Double.MIN_VALUE.toRawBits()))
        }
        return null
    }

    actual fun float(forKey: String): Float? {
        if (existsObject(forKey)) {
            return encSharedPrefs.getFloat(forKey, Float.MIN_VALUE)
        }
        return null
    }

    actual fun bool(forKey: String): Boolean? {
        if (existsObject(forKey)) {
            return encSharedPrefs.getBoolean(forKey, false)
        }
        return null
    }

    actual fun deleteObject(forKey: String): Boolean {
        return encSharedPrefs.edit().remove(forKey).commit()
    }

    actual fun clear() {
        encSharedPrefs.edit().clear().apply()
    }
}
