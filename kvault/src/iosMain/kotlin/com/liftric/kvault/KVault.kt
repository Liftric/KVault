package com.liftric.kvault

import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*
import platform.darwin.OSErr
import platform.darwin.noErr
import platform.posix.free
import kotlin.native.concurrent.freeze

/**
 * Keychain wrapper.
 *
 * @param serviceName Name of the service.
 * @param accessGroup Name of the access group. Used to share entries between apps.
 * @constructor Initiates a Keychain with the given properties.
 */
actual open class KVault(
    val serviceName: String? = null,
    val accessGroup: String? = null
) {
    private enum class Operation { Set, Get, Update, Delete }

    /**
     * Initiates a Keychain with the bundle identifier as the service name and without an access group.
     * If the bundle identifier is nil, it will fallback to `com.liftric.KVault`.
     */
    @Deprecated(
        """
            Will be removed in a future version, 
            please use the primary constructor. 
            Check your service name before migrating.
            """
        ,
        level = DeprecationLevel.WARNING
    )
    constructor() : this(Constants.BundleIdentifier, null)

    /**
     * Saves a string value in the Keychain.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: String): Boolean = memScoped {
        @Suppress("CAST_NEVER_SUCCEEDS")
        (value as NSString).dataUsingEncoding(NSUTF8StringEncoding)?.let {
            return set(key, it)
        } ?: run { return false }
    }

    /**
     * Saves an int value in the Keychain.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: Int): Boolean = memScoped{
        val number = NSNumber.numberWithInt(value)
        return set(key, NSKeyedArchiver.archivedDataWithRootObject(number))
    }

    /**
     * Saves a long value in the Keychain.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: Long): Boolean = memScoped{
        val number = NSNumber.numberWithLong(value)
        return set(key, NSKeyedArchiver.archivedDataWithRootObject(number))
    }

    /**
     * Saves a float value in the Keychain.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: Float): Boolean = memScoped{
        val number = NSNumber.numberWithFloat(value)
        return set(key, NSKeyedArchiver.archivedDataWithRootObject(number))
    }

    /**
     * Saves a double value in the Keychain.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: Double): Boolean = memScoped {
        val number = NSNumber.numberWithDouble(value)
        return set(key, NSKeyedArchiver.archivedDataWithRootObject(number))
    }

    /**
     * Saves a boolean value in the Keychain.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: Boolean): Boolean = memScoped {
        val number = NSNumber.numberWithBool(value)
        return set(key, NSKeyedArchiver.archivedDataWithRootObject(number))
    }

    /**
     * Returns the string value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun string(forKey: String): String? {
        data(forKey)?.let { data ->
            return NSString.create(data, NSUTF8StringEncoding) as String?
        } ?: run {
            return null
        }
    }

    /**
     * Returns the int value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun int(forKey: String): Int? {
        data(forKey)?.let {
            val number = NSKeyedUnarchiver.unarchiveObjectWithData(it) as NSNumber
            return number.intValue
        } ?: run {
            return null
        }
    }

    /**
     * Returns the long value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun long(forKey: String): Long? {
        data(forKey)?.let {
            val number = NSKeyedUnarchiver.unarchiveObjectWithData(it) as NSNumber
            return number.longValue
        } ?: run {
            return null
        }
    }

    /**
     * Returns the float value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun float(forKey: String): Float? {
        data(forKey)?.let {
            val number = NSKeyedUnarchiver.unarchiveObjectWithData(it) as NSNumber
            return number.floatValue
        } ?: run {
            return null
        }
    }

    /**
     * Returns the double value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun double(forKey: String): Double? {
        data(forKey)?.let {
            val number = NSKeyedUnarchiver.unarchiveObjectWithData(it) as NSNumber
            return number.doubleValue
        } ?: run {
            return null
        }
    }

    /**
     * Returns the boolean value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun bool(forKey: String): Boolean? {
        data(forKey)?.let {
            val number = NSKeyedUnarchiver.unarchiveObjectWithData(it) as NSNumber
            return number.boolValue
        } ?: run {
            return null
        }
    }

    /**
     * Checks if object with the given key exists in the Keychain.
     * @param forKey The key to query
     * @return True or false, depending on wether it is in the shared preferences or not
     */
    actual fun existsObject(forKey: String): Boolean = memScoped {
        retain(forKey) { (key) ->
            makeQuery(
                kSecClass to kSecClassGenericPassword,
                kSecAttrAccount to key,
                kSecReturnData to kCFBooleanFalse
            ) { query ->
                perform(Operation.Get, query)
            }
        }
    }

    /**
     * Deletes object with the given key from the Keychain.
     * @param forKey The key to query
     */
    actual fun deleteObject(forKey: String): Boolean = memScoped {
        retain(forKey) { (key) ->
            makeQuery(
                kSecClass to kSecClassGenericPassword,
                kSecAttrAccount to key
            ) { query ->
                perform(Operation.Delete, query)
            }
        }
    }

    /**
     * Deletes all objects.
     */
    actual fun clear(): Unit = memScoped {
        makeQuery(
            kSecClass to kSecClassGenericPassword
        ) { query ->
            perform(Operation.Delete, query)
        }
    }

    // ===============
    // PRIVATE METHODS
    // ===============

    private fun update(value: NSData, forKey: String): Boolean = memScoped {
        retain(forKey, value) { (key, value) ->
            makeQuery(
                kSecClass to kSecClassGenericPassword,
                kSecAttrAccount to key,
                kSecReturnData to kCFBooleanFalse
            ) { query ->
                makeQuery(
                    kSecValueData to value
                ) { updateQuery ->
                    perform(Operation.Update, query, updateQuery = updateQuery)
                }
            }
        }
    }

    private fun set(key: String, value: NSData): Boolean = memScoped {
        if(existsObject(key)) {
            update(value, key)
        } else {
            retain(key, value) { (key, value) ->
                makeQuery(
                    kSecClass to kSecClassGenericPassword,
                    kSecAttrAccount to key,
                    kSecValueData to value
                ) { query ->
                    perform(Operation.Set, query)
                }
            }
        }
    }

    private fun data(forKey: String): NSData? = memScoped {
        retain(forKey) { (key) ->
            makeQuery(
                kSecClass to kSecClassGenericPassword,
                kSecAttrAccount to key,
                kSecReturnData to kCFBooleanTrue,
                kSecMatchLimit to kSecMatchLimitOne
            ) { query ->
                val result = alloc<CFTypeRefVar>()
                if(perform(Operation.Get, query, result)) {
                    CFBridgingRelease(result.value) as? NSData
                } else {
                    null
                }
            }
        }
    }

    private fun <T> retain(vararg values: Any?, block: (List<CFTypeRef?>) -> T): T {
        val retained = arrayOf(*values).map { CFBridgingRetain(it) }
        try {
            return block(retained)
        } finally {
            retained.forEach { CFBridgingRelease(it) }
        }
    }

    private fun <T> MemScope.makeQuery(vararg pairs: Pair<CFStringRef?, CFTypeRef?>, operation: (CFDictionaryRef?) -> T): T {
        return retain(accessGroup, serviceName) { (accessGroup, serviceName) ->
            val map = mutableMapOf(*pairs)

            if (this@KVault.accessGroup != null) {
                map[kSecAttrAccessGroup] = accessGroup
            }
            if (this@KVault.serviceName != null) {
                map[kSecAttrService] = serviceName
            }

            val keys = allocArrayOf(*map.keys.toTypedArray())
            val values = allocArrayOf(*map.values.toTypedArray())

            val query = CFDictionaryCreate(
                kCFAllocatorDefault,
                keys.reinterpret(),
                values.reinterpret(),
                map.size.convert(),
                null,
                null
            )

            operation(query)
        }
    }

    private fun perform(
        operation: Operation,
        query: CFDictionaryRef?,
        result: CFTypeRefVar? = null,
        updateQuery: CFDictionaryRef? = null
    ): Boolean {
        return when (operation) {
            Operation.Set -> SecItemAdd(query, result?.ptr)
            Operation.Get -> SecItemCopyMatching(query, result?.ptr)
            Operation.Update -> SecItemUpdate(query, updateQuery).apply { CFRelease(updateQuery) }
            Operation.Delete -> SecItemDelete(query)
        }.apply {
            CFRelease(query)
        }.toUInt() == noErr
    }
}
