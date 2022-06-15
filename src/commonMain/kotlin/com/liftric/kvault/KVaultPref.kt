package com.liftric.kvault

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Abstraction for using properties as delegates
 * example:
 * var userId by stringPref(USER_ID_KEY)
 */
abstract class KVaultPref {

    abstract val kvault: KVault

    fun stringPref(key: String, defaultValue: String = "") = readWriteProp(
        defaultValue = defaultValue,
        getValue = {
            kvault.string(key)
        },
        setValue = { value ->
            kvault.set(key, value)
        }
    )

    fun booleanPref(key: String, defaultValue: Boolean = false) = readWriteProp(
        defaultValue = defaultValue,
        getValue = {
            kvault.bool(key)
        },
        setValue = { value ->
            kvault.set(key, value)
        }
    )

    fun longPref(key: String, defaultValue: Long = 0) = readWriteProp(
        defaultValue = defaultValue,
        getValue = {
            kvault.long(key)
        },
        setValue = { value ->
            kvault.set(key, value)
        }
    )

    fun intPref(key: String, defaultValue: Int = 0) = readWriteProp(
        defaultValue = defaultValue,
        getValue = {
            kvault.int(key)
        },
        setValue = { value ->
            kvault.set(key, value)
        }
    )

    fun floatPref(key: String, defaultValue: Float = 0.0f) = readWriteProp(
        defaultValue = defaultValue,
        getValue = {
            kvault.float(key)
        },
        setValue = { value ->
            kvault.set(key, value)
        }
    )

    fun doublePref(key: String, defaultValue: Double = 0.0) = readWriteProp(
        defaultValue = defaultValue,
        getValue = {
            kvault.double(key)
        },
        setValue = { value ->
            kvault.set(key, value)
        }
    )

    private fun <T> readWriteProp(
        defaultValue: T,
        getValue: () -> T?,
        setValue: (T) -> Unit
    ): ReadWriteProperty<KVaultPref, T> {
        return object : ReadWriteProperty<KVaultPref, T> {
            override fun getValue(thisRef: KVaultPref, property: KProperty<*>): T {
                return getValue() ?: defaultValue
            }

            override fun setValue(thisRef: KVaultPref, property: KProperty<*>, value: T) {
                setValue(value)
            }
        }
    }
}