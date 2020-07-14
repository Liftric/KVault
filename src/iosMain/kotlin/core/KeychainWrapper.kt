package core

import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*
import platform.darwin.noErr

class KeychainWrapper(val serviceName: String, val accessGroup: String?): KeychainWrappable {
    private enum class Operation { Set, Get, Update, Delete }

    var printsDebugOutput = false

    companion object {
        fun shared(): KeychainWrapper { return KeychainWrapper(
                serviceName = defaultServiceName(),
                accessGroup = null)
        }

        private fun defaultServiceName(): String {
            Constants.BundleIdentifier?.let {
                return it
            } ?: run {
                return Constants.CommonIdentifier
            }
        }
    }

    // ===============
    // SET OPERATIONS
    // ===============

    override fun set(value: String, forKey: String, withAccess: KeychainItemAccessibility?): Boolean {
        @Suppress("CAST_NEVER_SUCCEEDS")
        (value as NSString).dataUsingEncoding(NSUTF8StringEncoding)?.let {
            return set(it, forKey, withAccess)
        } ?: run { return false }
    }

    override fun set(value: Int, forKey: String, withAccess: KeychainItemAccessibility?): Boolean {
        val number = NSNumber.numberWithInt(value)
        return set(NSKeyedArchiver.archivedDataWithRootObject(number), forKey, withAccess)
    }

    override fun set(value: Long, forKey: String, withAccess: KeychainItemAccessibility?): Boolean {
        val number = NSNumber.numberWithLong(value)
        return set(NSKeyedArchiver.archivedDataWithRootObject(number), forKey, withAccess)
    }

    override fun set(value: Float, forKey: String, withAccess: KeychainItemAccessibility?): Boolean {
        val number = NSNumber.numberWithFloat(value)
        return set(NSKeyedArchiver.archivedDataWithRootObject(number), forKey, withAccess)
    }

    override fun set(value: Double, forKey: String, withAccess: KeychainItemAccessibility?): Boolean {
        val number = NSNumber.numberWithDouble(value)
        return set(NSKeyedArchiver.archivedDataWithRootObject(number), forKey, withAccess)
    }

    override fun set(value: Boolean, forKey: String, withAccess: KeychainItemAccessibility?): Boolean {
        val number = NSNumber.numberWithBool(value)
        return set(NSKeyedArchiver.archivedDataWithRootObject(number), forKey, withAccess)
    }

    // ===============
    // GET OPERATIONS
    // ===============

    override fun string(forKey: String): String? {
        data(forKey)?.let { data ->
            return NSString.create(data, NSUTF8StringEncoding) as String?
        } ?: run {
            return null
        }
    }

    override fun int(forKey: String): Int? {
        data(forKey)?.let {
            val number = NSKeyedUnarchiver.unarchiveObjectWithData(it) as NSNumber
            return number.intValue
        } ?: run {
            return null
        }
    }

    override fun long(forKey: String): Long? {
        data(forKey)?.let {
            val number = NSKeyedUnarchiver.unarchiveObjectWithData(it) as NSNumber
            return number.longValue
        } ?: run {
            return null
        }
    }

    override fun float(forKey: String): Float? {
        data(forKey)?.let {
            val number = NSKeyedUnarchiver.unarchiveObjectWithData(it) as NSNumber
            return number.floatValue
        } ?: run {
            return null
        }
    }

    override fun double(forKey: String): Double? {
        data(forKey)?.let {
            val number = NSKeyedUnarchiver.unarchiveObjectWithData(it) as NSNumber
            return number.doubleValue
        } ?: run {
            return null
        }
    }

    override fun bool(forKey: String): Boolean? {
        data(forKey)?.let {
            val number = NSKeyedUnarchiver.unarchiveObjectWithData(it) as NSNumber
            return number.boolValue
        } ?: run {
            return null
        }
    }

    override fun existsObject(forKey: String): Boolean {
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

    override fun deleteObject(forKey: String): Boolean {
        val query = CFDictionaryCreateMutable(null, capacity(3), null, null)
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(forKey))
        CFDictionaryAddValue(query, kSecAttrService, CFBridgingRetain(serviceName))
        return perform(Operation.Delete, query)
    }

    override fun clear() {
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

    private fun set(value: NSData, forKey: String, withAccess: KeychainItemAccessibility?): Boolean {
        val capacity = capacity(4, withAccess != null)
        val query = CFDictionaryCreateMutable(null, capacity, null, null)
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrAccount, CFBridgingRetain(forKey))
        CFDictionaryAddValue(query, kSecValueData, CFBridgingRetain(value))
        CFDictionaryAddValue(query, kSecAttrService, CFBridgingRetain(serviceName))
        addAccessibilityIfSet(query, withAccess)

        return if (existsObject(forKey)) {
            update(value, forKey)
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

    private fun capacity(base: CFIndex, hasAccessibilityItem: Boolean? = null): CFIndex {
        var capacity = base
        accessGroup?.let { capacity += 1 }
        hasAccessibilityItem?.let { capacity += 1 }
        return capacity
    }

    private fun addAccessGroupIfSet(query: CFMutableDictionaryRef?) {
        accessGroup?.let {
            CFDictionaryAddValue(query, kSecAttrAccessGroup, CFBridgingRetain(it))
        }
    }

    private fun addAccessibilityIfSet(query: CFMutableDictionaryRef?, withAccess: KeychainItemAccessibility?) {
        withAccess?.let {
            CFDictionaryAddValue(query, kSecAttrAccessible, it.attribute)
        }
    }

    private fun perform(operation: Operation, query: CFMutableDictionaryRef?,
                        result: CFTypeRefVar? = null,
                        updateQuery: CFDictionaryRef? = null,
                        verbose: Boolean? = true): Boolean {
        addAccessGroupIfSet(query)

        val status = when (operation) {
            Operation.Set -> SecItemAdd(query, result?.ptr)
            Operation.Get -> SecItemCopyMatching(query, result?.ptr)
            Operation.Update -> SecItemUpdate(query, updateQuery)
            Operation.Delete -> SecItemDelete(query)
        }

        return if (status.toUInt() == noErr) { true } else {
            val error = SecCopyErrorMessageString(status, null)
            val errorMessage = CFBridgingRelease(error)
            if (printsDebugOutput && verbose!!) {
                println("Operation -> ${operation.name}")
                println("$errorMessage")
            }
            false
        }
    }
}