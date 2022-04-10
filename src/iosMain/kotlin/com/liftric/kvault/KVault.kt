package com.liftric.kvault

import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*
import platform.darwin.OSStatus
import platform.darwin.noErr

/**
 * Keychain wrapper.
 * Note: Using the deprecated init() the service name was the apps bundle identifier or if null "com.liftric.KVault".
 *
 * @param serviceName Name of the service. Used to categories entries.
 * @param accessGroup Name of the access group. Used to share entries between apps.
 * @constructor Initiates a Keychain with the given parameters.
 */
actual open class KVault(
    val serviceName: String? = null,
    val accessGroup: String? = null
) {
    /**
     * Saves a string value in the Keychain.
     * @param key The key to store
     * @param stringValue The value to store
     * @return True or false, depending on whether the value has been stored in the Keychain
     */
    actual fun set(key: String, stringValue: String): Boolean {
        return addOrUpdate(key, stringValue.toNSData())
    }

    /**
     * Saves an int value in the Keychain.
     * @param key The key to store
     * @param intValue The value to store
     * @return True or false, depending on whether the value has been stored in the Keychain
     */
    actual fun set(key: String, intValue: Int): Boolean {
        return addOrUpdate(key, NSNumber(int = intValue).toNSData())
    }

    /**
     * Saves a long value in the Keychain.
     * @param key The key to store
     * @param longValue The value to store
     * @return True or false, depending on whether the value has been stored in the Keychain
     */
    actual fun set(key: String, longValue: Long): Boolean {
        return addOrUpdate(key, NSNumber(long = longValue).toNSData())
    }

    /**
     * Saves a float value in the Keychain.
     * @param key The key to store
     * @param floatValue The value to store
     * @return True or false, depending on whether the value has been stored in the Keychain
     */
    actual fun set(key: String, floatValue: Float): Boolean {
        return addOrUpdate(key, NSNumber(float = floatValue).toNSData())
    }

    /**
     * Saves a double value in the Keychain.
     * @param key The key to store
     * @param doubleValue The value to store
     * @return True or false, depending on whether the value has been stored in the Keychain
     */
    actual fun set(key: String, doubleValue: Double): Boolean {
        return addOrUpdate(key, NSNumber(double = doubleValue).toNSData())
    }

    /**
     * Saves a boolean value in the Keychain.
     * @param key The key to store
     * @param boolValue The value to store
     * @return True or false, depending on whether the value has been stored in the Keychain
     */
    actual fun set(key: String, boolValue: Boolean): Boolean {
        return addOrUpdate(key, NSNumber(bool = boolValue).toNSData())
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
     * @return The stored int value, or null if it is missing
     */
    actual fun int(forKey: String): Int? {
        return value(forKey)?.toNSNumber()?.intValue
    }

    /**
     * Returns the long value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored long value, or null if it is missing
     */
    actual fun long(forKey: String): Long? {
        return value(forKey)?.toNSNumber()?.longValue
    }

    /**
     * Returns the float value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored float value, or null if it is missing
     */
    actual fun float(forKey: String): Float? {
        return value(forKey)?.toNSNumber()?.floatValue
    }

    /**
     * Returns the double value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored double value, or null if it is missing
     */
    actual fun double(forKey: String): Double? {
        return value(forKey)?.toNSNumber()?.doubleValue
    }

    /**
     * Returns the boolean value of an object in the Keychain.
     * @param forKey The key to query
     * @return The stored boolean value, or null if it is missing
     */
    actual fun bool(forKey: String): Boolean? {
        return value(forKey)?.toNSNumber()?.boolValue
    }

    /**
     * Returns all keys of the objects in the Keychain.
     * @return A list with all keys
     */
    @Suppress("UNCHECKED_CAST")
    actual fun allKeys(): List<String> = context {
        val query = query(
            kSecClass to kSecClassGenericPassword,
            kSecReturnAttributes to kCFBooleanTrue,
            kSecMatchLimit to kSecMatchLimitAll
        )

        memScoped {
            val result = alloc<CFTypeRefVar>()
            val isValid = SecItemCopyMatching(query, result.ptr).validate()
            if (isValid) {
                val items = CFBridgingRelease(result.value) as? List<Map<String, Any>>
                items?.mapNotNull { it["acct"] as? String } ?: listOf()
            } else {
                listOf()
            }
        }
    }

    /**
     * Checks if object with the given key exists in the Keychain.
     * @param forKey The key to query
     * @return True or false, depending on whether it is in the Keychain or not
     */
    actual fun existsObject(forKey: String): Boolean = context(forKey) { (account) ->
        val query = query(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to account,
            kSecReturnData to kCFBooleanFalse
        )

        SecItemCopyMatching(query, null)
            .validate()
    }

    /**
     * Deletes object with the given key from the Keychain.
     * @param forKey The key to query
     * @return True or false, depending on whether the object has been deleted
     */
    actual fun deleteObject(forKey: String): Boolean = context(forKey) { (account) ->
        val query = query(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to account
        )

        SecItemDelete(query)
            .validate()
    }

    /**
     * Deletes all objects.
     * If the service name and/or the access group are null, all items in the apps'
     * Keychain will be deleted.
     * @return True or false, depending on whether the objects have been deleted
     */
    actual fun clear(): Boolean = context {
        val query = query(
            kSecClass to kSecClassGenericPassword
        )

        SecItemDelete(query)
            .validate()
    }

    // ===============
    // PRIVATE METHODS
    // ===============

    private fun addOrUpdate(key: String, value: NSData?): Boolean {
        return if(existsObject(key)) {
            update(key, value)
        } else {
            add(key, value)
        }
    }

    private fun add(key: String, value: NSData?): Boolean = context(key, value) { (account, data) ->
        val query = query(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to account,
            kSecValueData to data
        )

        SecItemAdd(query, null)
            .validate()
    }

    private fun update(key: String, value: Any?): Boolean = context(key, value) { (account, data) ->
        val query = query(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to account,
            kSecReturnData to kCFBooleanFalse
        )

        val updateQuery = query(
            kSecValueData to data
        )

        SecItemUpdate(query, updateQuery)
            .validate()
    }

    private fun value(forKey: String): NSData? = context(forKey) { (account) ->
        val query = query(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to account,
            kSecReturnData to kCFBooleanTrue,
            kSecMatchLimit to kSecMatchLimitOne
        )

        memScoped {
            val result = alloc<CFTypeRefVar>()
            SecItemCopyMatching(query, result.ptr)
            CFBridgingRelease(result.value) as? NSData
        }
    }

    // ========
    // HELPERS
    // ========

    private class Context(val refs: Map<CFStringRef?, CFTypeRef?>) {
        fun query(vararg pairs: Pair<CFStringRef?, CFTypeRef?>): CFDictionaryRef? {
            val map = mapOf(*pairs).plus(refs.filter { it.value != null })
            return CFDictionaryCreateMutable(
                null, map.size.convert(), null, null
            ).apply {
                map.entries.forEach { CFDictionaryAddValue(this, it.key, it.value) }
            }.apply {
                CFAutorelease(this)
            }
        }
    }

    private fun <T> context(vararg values: Any?, block: Context.(List<CFTypeRef?>) -> T): T {
        val standard = mapOf(
            kSecAttrService to CFBridgingRetain(serviceName),
            kSecAttrAccessGroup to CFBridgingRetain(accessGroup)
        )
        val custom = arrayOf(*values).map { CFBridgingRetain(it) }
        return block.invoke(Context(standard), custom).apply {
            standard.values.plus(custom).forEach { CFBridgingRelease(it) }
        }
    }

    private fun String.toNSData(): NSData? = NSString.create(string = this).dataUsingEncoding(NSUTF8StringEncoding)
    private fun NSNumber.toNSData() = NSKeyedArchiver.archivedDataWithRootObject(this)
    private fun NSData.toNSNumber() = NSKeyedUnarchiver.unarchiveObjectWithData(this) as? NSNumber

    private val NSData.stringValue: String?
        get() = NSString.create(this, NSUTF8StringEncoding) as String?

    private fun OSStatus.validate(): Boolean = toUInt() == noErr
}
