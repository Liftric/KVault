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
     * @param stringValue The value to store
     */
    actual fun set(key: String, stringValue: String): Boolean {
        return add(key, stringValue.toNSData())
    }

    /**
     * Saves an int value in the Keychain.
     * @param key The key to store
     * @param intValue The value to store
     */
    actual fun set(key: String, intValue: Int): Boolean {
        return add(key, NSNumber(int = intValue).toNSData())
    }

    /**
     * Saves a long value in the Keychain.
     * @param key The key to store
     * @param longValue The value to store
     */
    actual fun set(key: String, longValue: Long): Boolean {
        return add(key, NSNumber(long = longValue).toNSData())
    }

    /**
     * Saves a float value in the Keychain.
     * @param key The key to store
     * @param floatValue The value to store
     */
    actual fun set(key: String, floatValue: Float): Boolean {
        return add(key, NSNumber(float = floatValue).toNSData())
    }

    /**
     * Saves a double value in the Keychain.
     * @param key The key to store
     * @param doubleValue The value to store
     */
    actual fun set(key: String, doubleValue: Double): Boolean {
        return add(key, NSNumber(double = doubleValue).toNSData())
    }

    /**
     * Saves a boolean value in the Keychain.
     * @param key The key to store
     * @param boolValue The value to store
     */
    actual fun set(key: String, boolValue: Boolean): Boolean {
        return add(key, NSNumber(bool = boolValue).toNSData())
    }

    /**
     * Returns the string value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun string(forKey: String): String? {
        return value(forKey)?.stringValue
    }

    /**
     * Returns the int value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun int(forKey: String): Int? {
        return value(forKey)?.toNSNumber()?.intValue
    }

    /**
     * Returns the long value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun long(forKey: String): Long? {
        return value(forKey)?.toNSNumber()?.longValue
    }

    /**
     * Returns the float value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun float(forKey: String): Float? {
        return value(forKey)?.toNSNumber()?.floatValue
    }

    /**
     * Returns the double value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun double(forKey: String): Double? {
        return value(forKey)?.toNSNumber()?.doubleValue
    }

    /**
     * Returns the boolean value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored string value, or null if it is missing
     */
    actual fun bool(forKey: String): Boolean? {
        return value(forKey)?.toNSNumber()?.boolValue
    }

    /**
     * Checks if object with the given key exists in the Keychain.
     * @param forKey The key to query
     * @return True or false, depending on whether it is in the Keychain or not
     */
    actual fun existsObject(forKey: String): Boolean = retain(forKey) { (key) ->
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

    /**
     * Deletes object with the given key from the Keychain.
     * @param forKey The key to query
     */
    actual fun deleteObject(forKey: String): Boolean = retain(forKey) { (key) ->
        makeQuery(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to key
        ) { query ->
            SecItemDelete(query)
                .release(query)
                .isValid()
        }
    }

    /**
     * Deletes all objects.
     */
    actual fun clear() {
        return makeQuery(
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

    private fun update(value: Any?, forKey: String): Boolean = retain(forKey, value) { (key, value) ->
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

    private fun add(key: String, value: NSData?): Boolean {
        return if(existsObject(key)) {
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

    private fun value(forKey: String): NSData? = retain(forKey) { (key) ->
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

    private fun <T> retain(vararg values: Any?, block: MemScope.(List<CFTypeRef?>) -> T): T = memScoped {
        val retained = arrayOf(*values.copyOf()).map { CFBridgingRetain(it) }
        block(retained).apply {
            retained.forEach { CFBridgingRelease(it) }
        }
    }

    private fun <T> makeQuery(
        vararg pairs: Pair<CFStringRef?, CFTypeRef?>,
        action: (CFDictionaryRef?) -> T
    ): T = retain(accessGroup, serviceName) { (accessGroup, serviceName) ->
        val map = mutableMapOf(*pairs)

        if (accessGroup != null) {
            map[kSecAttrAccessGroup] = accessGroup
        }
        if (serviceName != null) {
            map[kSecAttrService] = serviceName
        }

        CFDictionaryCreateMutable(
            null, map.size.convert(), null, null
        ).apply {
            map.entries.forEach { CFDictionaryAddValue(this, it.key, it.value) }
        }.run {
            action(this)
        }
    }

    private fun String.toNSData(): NSData? = NSString.create(string = this).dataUsingEncoding(NSUTF8StringEncoding)
    private fun NSNumber.toNSData() = NSKeyedArchiver.archivedDataWithRootObject(this)
    private fun NSData.toNSNumber() = NSKeyedUnarchiver.unarchiveObjectWithData(this) as? NSNumber
    private val NSData.stringValue: String?
        get() = NSString.create(this, NSUTF8StringEncoding) as String?

    private fun OSStatus.release(vararg queries: CFTypeRef?) = apply { queries.forEach { CFRelease(it) } }
    private fun OSStatus.isValid(): Boolean = toUInt() == noErr
}
