package com.liftric.kvault

import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*
import platform.darwin.noErr

actual class KVault() {
    private enum class Operation { Set, Get, Update, Delete }

    var printsDebugOutput = true

    private var serviceName = defaultServiceName()
    private var accessGroup: String? = null

    constructor(serviceName: String, accessGroup: String? = null): this() {
        this.serviceName = serviceName
        this.accessGroup = accessGroup
    }

    // ===============
    // SET OPERATIONS
    // ===============

    actual fun set(key: String, value: String): Boolean {
        @Suppress("CAST_NEVER_SUCCEEDS")
        (value as NSString).dataUsingEncoding(NSUTF8StringEncoding)?.let {
            return set(key, it)
        } ?: run { return false }
    }

    actual fun set(key: String, value: Int): Boolean {
        val number = NSNumber.numberWithInt(value)
        return set(key, NSKeyedArchiver.archivedDataWithRootObject(number))
    }

    actual fun set(key: String, value: Long): Boolean {
        val number = NSNumber.numberWithLong(value)
        return set(key, NSKeyedArchiver.archivedDataWithRootObject(number))
    }

    actual fun set(key: String, value: Float): Boolean {
        val number = NSNumber.numberWithFloat(value)
        return set(key, NSKeyedArchiver.archivedDataWithRootObject(number))
    }

    actual fun set(key: String, value: Double): Boolean {
        val number = NSNumber.numberWithDouble(value)
        return set(key, NSKeyedArchiver.archivedDataWithRootObject(number))
    }

    actual fun set(key: String, value: Boolean): Boolean {
        val number = NSNumber.numberWithBool(value)
        return set(key, NSKeyedArchiver.archivedDataWithRootObject(number))
    }

    // ===============
    // GET OPERATIONS
    // ===============

    actual fun string(forKey: String): String? {
        data(forKey)?.let { data ->
            return NSString.create(data, NSUTF8StringEncoding) as String?
        } ?: run {
            return null
        }
    }

    actual fun int(forKey: String): Int? {
        data(forKey)?.let {
            val number = NSKeyedUnarchiver.unarchiveObjectWithData(it) as NSNumber
            return number.intValue
        } ?: run {
            return null
        }
    }

    actual fun long(forKey: String): Long? {
        data(forKey)?.let {
            val number = NSKeyedUnarchiver.unarchiveObjectWithData(it) as NSNumber
            return number.longValue
        } ?: run {
            return null
        }
    }

    actual fun float(forKey: String): Float? {
        data(forKey)?.let {
            val number = NSKeyedUnarchiver.unarchiveObjectWithData(it) as NSNumber
            return number.floatValue
        } ?: run {
            return null
        }
    }

    actual fun double(forKey: String): Double? {
        data(forKey)?.let {
            val number = NSKeyedUnarchiver.unarchiveObjectWithData(it) as NSNumber
            return number.doubleValue
        } ?: run {
            return null
        }
    }

    actual fun bool(forKey: String): Boolean? {
        data(forKey)?.let {
            val number = NSKeyedUnarchiver.unarchiveObjectWithData(it) as NSNumber
            return number.boolValue
        } ?: run {
            return null
        }
    }

    actual fun existsObject(forKey: String): Boolean {
        val query = CFDictionaryCreateMutable(null, capacity(4), null, null)
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(forKey))
        CFDictionaryAddValue(query, kSecReturnData, kCFBooleanFalse)
        CFDictionaryAddValue(query, kSecAttrService, CFBridgingRetain(serviceName))

        memScoped {
            val result = alloc<CFTypeRefVar>()
            if (perform(Operation.Get, query, result, verbose = false)) {
                return true
            }
        }

        return false
    }

    // ==================
    // DELETE OPERATIONS
    // ==================

    actual fun deleteObject(forKey: String): Boolean {
        val query = CFDictionaryCreateMutable(null, capacity(3), null, null)
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(forKey))
        CFDictionaryAddValue(query, kSecAttrService, CFBridgingRetain(serviceName))
        return perform(Operation.Delete, query)
    }

    actual fun clear() {
        val query = CFDictionaryCreateMutable(null, capacity(2), null, null)
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrService, CFBridgingRetain(serviceName))
        perform(Operation.Delete, query)
    }

    // =================
    // UPDATE OPERATIONS
    // =================

    private fun update(value: NSData, forKey: String): Boolean {
        val query = CFDictionaryCreateMutable(null, 4, null, null)
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(forKey))
        CFDictionaryAddValue(query, kSecReturnData, kCFBooleanFalse)
        CFDictionaryAddValue(query, kSecAttrService, CFBridgingRetain(serviceName))

        val updateQuery = CFDictionaryCreateMutable(null, 1, null, null)
        CFDictionaryAddValue(updateQuery, kSecValueData, CFBridgingRetain(value))

        return perform(Operation.Update, query, updateQuery = updateQuery)
    }

    // ===============
    // HELPER METHODS
    // ===============

    private fun set(key: String, value: NSData): Boolean {
        val capacity = capacity(4)
        val query = CFDictionaryCreateMutable(null, capacity, null, null)
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(key))
        CFDictionaryAddValue(query, kSecValueData, CFBridgingRetain(value))
        CFDictionaryAddValue(query, kSecAttrService, CFBridgingRetain(serviceName))

        return if (existsObject(key)) {
            update(value, key)
        } else {
            perform(Operation.Set, query)
        }
    }

    private fun data(forKey: String): NSData? {
        val query = CFDictionaryCreateMutable(null, capacity(5), null, null)
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(forKey))
        CFDictionaryAddValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionaryAddValue(query, kSecMatchLimit, kSecMatchLimitOne)
        CFDictionaryAddValue(query, kSecAttrService, CFBridgingRetain(serviceName))

        memScoped {
            val result = alloc<CFTypeRefVar>()
            if (perform(Operation.Get, query, result)) {
                return CFBridgingRelease(result.value) as NSData
            }
        }

        return null
    }

    private fun capacity(base: CFIndex): CFIndex {
        var capacity = base
        accessGroup?.let { capacity += 1 }
        return capacity
    }

    private fun addAccessGroupIfSet(query: CFMutableDictionaryRef?) {
        accessGroup?.let {
            CFDictionaryAddValue(query, kSecAttrAccessGroup, CFBridgingRetain(it))
        }
    }

    private fun perform(
        operation: Operation, query: CFMutableDictionaryRef?,
        result: CFTypeRefVar? = null,
        updateQuery: CFDictionaryRef? = null,
        verbose: Boolean? = true
    ): Boolean {
        addAccessGroupIfSet(query)

        val status = when (operation) {
            Operation.Set -> SecItemAdd(query, result?.ptr)
            Operation.Get -> SecItemCopyMatching(query, result?.ptr)
            Operation.Update -> SecItemUpdate(query, updateQuery)
            Operation.Delete -> SecItemDelete(query)
        }

        return if (status.toUInt() == noErr) {
            true
        } else {
            val error = SecCopyErrorMessageString(status, null)
            val errorMessage = CFBridgingRelease(error)
            if (printsDebugOutput && verbose!!) {
                println("Operation -> ${operation.name}")
                println("$errorMessage")
            }
            false
        }
    }

    private fun defaultServiceName(): String {
        Constants.BundleIdentifier?.let {
            return it
        } ?: run {
            return Constants.DefaultIdentifier
        }
    }
}
