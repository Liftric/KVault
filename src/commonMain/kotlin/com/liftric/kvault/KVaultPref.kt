package com.liftric.kvault

/**
 * Abstraction for using properties as delegates
 * example:
 * var userId by stringPref(USER_ID_KEY)
 * @param S is a class that is going to inherit that class
 * @param key is totally optional, it will use property name if you dont provide it
 */
abstract class KVaultPref<S> {
    abstract val kvault: KVault
    fun stringPref(key: String? = null, defaultValue: String = "") = kvault.stringPref<S>(key, defaultValue)
    fun stringPrefNullable(key: String? = null, defaultValue: String? = null) =
        kvault.stringPrefNullable<S>(key, defaultValue)

    fun booleanPref(key: String? = null, defaultValue: Boolean = false) =
        kvault.booleanPref<S>(key, defaultValue)
    fun booleanPrefNullable(key: String? = null, defaultValue: Boolean? = null) =
        kvault.booleanPrefNullable<S>(key, defaultValue)

    fun longPref(key: String? = null, defaultValue: Long = 0) = kvault.longPref<S>(key, defaultValue)
    fun longPrefNullable(key: String? = null, defaultValue: Long? = null) =
        kvault.longPrefNullable<S>(key, defaultValue)

    fun intPref(key: String? = null, defaultValue: Int = 0) = kvault.intPref<S>(key, defaultValue)
    fun intPrefNullable(key: String? = null, defaultValue: Int? = null) =
        kvault.intPrefNullable<S>(key, defaultValue)

    fun floatPref(key: String? = null, defaultValue: Float = 0.0f) = kvault.floatPref<S>(key, defaultValue)
    fun floatPrefNullable(key: String? = null, defaultValue: Float? = null) =
        kvault.floatPrefNullable<S>(key, defaultValue)

    fun doublePref(key: String? = null, defaultValue: Double = 0.0) =
        kvault.doublePrefNullable<S>(key, defaultValue)

    fun doublePrefNullable(key: String? = null, defaultValue: Double? = null) =
        kvault.doublePrefNullable<S>(key, defaultValue)

    fun exists(key: String? = null) =
        kvault.exists<S>(key)
}