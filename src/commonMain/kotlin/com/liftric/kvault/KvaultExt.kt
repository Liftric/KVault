package com.liftric.kvault

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <S> KVault.stringPref(key: String? = null, defaultValue: String = "") = readWriteProp<S, String>(
    defaultValue = defaultValue,
    getValue = { prop ->
        val newKey = key ?: prop.name
        this.string(newKey)
    },
    setValue = { prop, value ->
        val newKey = key ?: prop.name
        this.set(newKey, value)
    }
)

fun <S> KVault.stringPrefNullable(key: String? = null, defaultValue: String? = null) =
    readWritePropNullable<S, String>(
        defaultValue = defaultValue,
        getValue = { prop ->
            val newKey = key ?: prop.name
            this.string(newKey)
        },
        setValue = { prop, value ->
            val newKey = key ?: prop.name
            this.set(newKey, value)
        }
    )

fun <S> KVault.booleanPref(key: String? = null, defaultValue: Boolean = false) = readWriteProp<S, Boolean>(
    defaultValue = defaultValue,
    getValue = { prop ->
        val newKey = key ?: prop.name
        this.bool(newKey)
    },
    setValue = { prop, value ->
        val newKey = key ?: prop.name
        this.set(newKey, value)
    }
)

fun <S> KVault.booleanPrefNullable(key: String? = null, defaultValue: Boolean? = null) =
    readWritePropNullable<S, Boolean>(
        defaultValue = defaultValue,
        getValue = { prop ->
            val newKey = key ?: prop.name
            this.bool(newKey)
        },
        setValue = { prop, value ->
            val newKey = key ?: prop.name
            this.set(newKey, value)
        }
    )

fun <S> KVault.longPref(key: String? = null, defaultValue: Long = 0) = readWriteProp<S, Long>(
    defaultValue = defaultValue,
    getValue = { prop ->
        val newKey = key ?: prop.name
        this.long(newKey)
    },
    setValue = { prop, value ->
        val newKey = key ?: prop.name
        this.set(newKey, value)
    }
)

fun <S> KVault.longPrefNullable(key: String? = null, defaultValue: Long? = null) =
    readWritePropNullable<S, Long>(
        defaultValue = defaultValue,
        getValue = { prop ->
            val newKey = key ?: prop.name
            this.long(newKey)
        },
        setValue = { prop, value ->
            val newKey = key ?: prop.name
            this.set(newKey, value)
        }
    )

fun <S> KVault.intPref(key: String? = null, defaultValue: Int = 0) = readWriteProp<S, Int>(
    defaultValue = defaultValue,
    getValue = { prop ->
        val newKey = key ?: prop.name
        this.int(newKey)
    },
    setValue = { prop, value ->
        val newKey = key ?: prop.name
        this.set(newKey, value)
    }
)

fun <S> KVault.intPrefNullable(key: String? = null, defaultValue: Int? = null) = readWritePropNullable<S, Int>(
    defaultValue = defaultValue,
    getValue = { prop ->
        val newKey = key ?: prop.name
        this.int(newKey)
    },
    setValue = { prop, value ->
        val newKey = key ?: prop.name
        this.set(newKey, value)
    }
)

fun <S> KVault.floatPref(key: String? = null, defaultValue: Float = 0.0f) = readWriteProp<S, Float>(
    defaultValue = defaultValue,
    getValue = { prop ->
        val newKey = key ?: prop.name
        this.float(newKey)
    },
    setValue = { prop, value ->
        val newKey = key ?: prop.name
        this.set(newKey, value)
    }
)

fun <S> KVault.floatPrefNullable(key: String? = null, defaultValue: Float? = null) =
    readWritePropNullable<S, Float>(
        defaultValue = defaultValue,
        getValue = { prop ->
            val newKey = key ?: prop.name
            this.float(newKey)
        },
        setValue = { prop, value ->
            val newKey = key ?: prop.name
            this.set(newKey, value)
        }
    )

fun <S> KVault.doublePref(key: String? = null, defaultValue: Double = 0.0) = readWriteProp<S, Double>(
    defaultValue = defaultValue,
    getValue = { prop ->
        val newKey = key ?: prop.name
        this.double(newKey)
    },
    setValue = { prop, value ->
        val newKey = key ?: prop.name
        this.set(newKey, value)
    }
)

fun <S> KVault.doublePrefNullable(key: String? = null, defaultValue: Double? = null) =
    readWritePropNullable<S, Double>(
        defaultValue = defaultValue,
        getValue = { prop ->
            val newKey = key ?: prop.name
            this.double(newKey)
        },
        setValue = { prop, value ->
            val newKey = key ?: prop.name
            this.set(newKey, value)
        }
    )

fun <S> KVault.exists(key: String? = null) = readProp<S, Boolean>(
    defaultValue = false,
    getValue = { prop ->
        val newKey = key ?: prop.name
        this.existsObject(newKey)
    }
)

private fun <S, T> readProp(
    defaultValue: T,
    getValue: (KProperty<*>) -> T?,
): ReadOnlyProperty<S, T> {
    return ReadOnlyProperty<S, T> { _, property -> getValue(property) ?: defaultValue }
}

private fun <S, T> readWriteProp(
    defaultValue: T,
    getValue: (KProperty<*>) -> T?,
    setValue: (KProperty<*>, T) -> Unit
): ReadWriteProperty<S, T> {
    return object : ReadWriteProperty<S, T> {
        override fun getValue(thisRef: S, property: KProperty<*>): T {
            return getValue(property) ?: defaultValue
        }

        override fun setValue(thisRef: S, property: KProperty<*>, value: T) {
            setValue(property, value)
        }
    }
}

private fun <S, T> readWritePropNullable(
    defaultValue: T?,
    getValue: (KProperty<*>) -> T?,
    setValue: (KProperty<*>, T) -> Unit
): ReadWriteProperty<S, T?> {
    return object : ReadWriteProperty<S, T?> {
        override fun getValue(thisRef: S, property: KProperty<*>): T? {
            return getValue(property) ?: defaultValue
        }

        override fun setValue(thisRef: S, property: KProperty<*>, value: T?) {
            value?.let {
                setValue(property, it)
            }
        }
    }
}