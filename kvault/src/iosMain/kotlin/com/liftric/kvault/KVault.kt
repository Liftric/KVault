package com.liftric.kvault

import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*
import platform.darwin.OSStatus
import platform.darwin.noErr

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
        return value.toNSData()?.let { add(key, it) } ?: run { false }
    }

    /**
     * Saves an int value in the Keychain.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: Int): Boolean = memScoped{
        return add(key, NSKeyedArchiver.encode(NSNumber.numberWithInt(value)))
    }

    /**
     * Saves a long value in the Keychain.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: Long): Boolean = memScoped{
        return add(key, NSKeyedArchiver.encode(NSNumber.numberWithLong(value)))
    }

    /**
     * Saves a float value in the Keychain.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: Float): Boolean = memScoped{
        return add(key, NSKeyedArchiver.encode(NSNumber.numberWithFloat(value)))
    }

    /**
     * Saves a double value in the Keychain.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: Double): Boolean = memScoped {
        return add(key, NSKeyedArchiver.encode(NSNumber.numberWithDouble(value)))
    }

    /**
     * Saves a boolean value in the Keychain.
     * @param key The key to store
     * @param value The value to store
     */
    actual fun set(key: String, value: Boolean): Boolean = memScoped {
        return add(key, NSKeyedArchiver.encode(NSNumber.numberWithBool(value)))
    }

    /**
     * Returns the string value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun string(forKey: String): String? {
        return value(forKey)?.let { data ->
            NSString.create(data, NSUTF8StringEncoding) as String?
        }
    }

    /**
     * Returns the int value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun int(forKey: String): Int? {
        return value(forKey)?.let {
            (NSKeyedUnarchiver.decode(it))?.intValue
        }
    }

    /**
     * Returns the long value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun long(forKey: String): Long? {
        return value(forKey)?.let {
            (NSKeyedUnarchiver.decode(it))?.longValue
        }
    }

    /**
     * Returns the float value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun float(forKey: String): Float? {
        return value(forKey)?.let {
            (NSKeyedUnarchiver.decode(it))?.floatValue
        }
    }

    /**
     * Returns the double value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun double(forKey: String): Double? {
        return value(forKey)?.let {
            (NSKeyedUnarchiver.decode(it))?.doubleValue
        }
    }

    /**
     * Returns the boolean value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun bool(forKey: String): Boolean? {
        return value(forKey)?.let {
            (NSKeyedUnarchiver.decode(it))?.boolValue
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
                SecItemCopyMatching(query, null)
                    .release(query)
                    .isValid()
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
                SecItemDelete(query)
                    .release(query)
                    .isValid()
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
            SecItemDelete(query)
                .release(query)
                .isValid()
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
                    SecItemUpdate(query, updateQuery)
                        .release(query, updateQuery)
                        .isValid()
                }
            }
        }
    }

    private fun add(key: String, value: NSData): Boolean = memScoped {
        if(existsObject(key)) {
            update(value, key)
        } else {
            retain(key, value) { (key, value) ->
                makeQuery(
                    kSecClass to kSecClassGenericPassword,
                    kSecAttrAccount to key,
                    kSecValueData to value
                ) { query ->
                    SecItemAdd(query, null)
                        .release(query)
                        .isValid()
                }
            }
        }
    }

    private fun value(forKey: String): NSData? = memScoped {
        retain(forKey) { (key) ->
            makeQuery(
                kSecClass to kSecClassGenericPassword,
                kSecAttrAccount to key,
                kSecReturnData to kCFBooleanTrue,
                kSecMatchLimit to kSecMatchLimitOne
            ) { query ->
                val result = alloc<CFTypeRefVar>()
                SecItemCopyMatching(query, result.ptr)
                    .release(query)
                    .isValid()
                CFBridgingRelease(result.value) as? NSData
            }
        }
    }

    private fun <T> retain(vararg values: Any?, block: (List<CFTypeRef?>) -> T): T {
        val retained = arrayOf(*values).map { CFBridgingRetain(it) }
        return block(retained).apply {
            retained.forEach { CFBridgingRelease(it) }
        }
    }

    private fun <T> MemScope.makeQuery(vararg pairs: Pair<CFStringRef?, CFTypeRef?>, block: (CFDictionaryRef?) -> T): T {
        return retain(accessGroup, serviceName) { (accessGroup, serviceName) ->
            val map = mutableMapOf(*pairs)

            if (accessGroup != null) {
                map[kSecAttrAccessGroup] = accessGroup
            }
            if (serviceName != null) {
                map[kSecAttrService] = serviceName
            }

            val keys = allocArrayOf(*map.keys.toTypedArray())
            val values = allocArrayOf(*map.values.toTypedArray())

            CFDictionaryCreate(
                null,
                keys.reinterpret(),
                values.reinterpret(),
                map.size.convert(),
                null,
                null
            ).run {
                block(this)
            }
        }
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun String.toNSData(): NSData? = (this as NSString).dataUsingEncoding(NSUTF8StringEncoding)

    private fun NSKeyedArchiver.Companion.encode(value: Any): NSData = archivedDataWithRootObject(value)
    private fun NSKeyedUnarchiver.Companion.decode(data: NSData): NSNumber? = unarchiveObjectWithData(data) as? NSNumber

    private fun OSStatus.release(vararg queries: CFTypeRef?) = apply { queries.forEach { CFRelease(it) } }
    private fun OSStatus.isValid(): Boolean = toUInt() == noErr
}