package core

import platform.Security.*
import platform.Foundation.*

object Constants {
    const val CommonIdentifier = "com.liftric.KeychainWrapper"
    val BundleIdentifier = NSBundle.mainBundle.bundleIdentifier

    object Accessible {
        val AfterFirstUnlock = kSecAttrAccessibleAfterFirstUnlock
        val AfterFirstUnlockThisDeviceOnly = kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
        val WhenPasscodeSetThisDeviceOnly = kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly
        val WhenUnlocked = kSecAttrAccessibleWhenUnlocked
        val WhenUnlockedThisDeviceOnly = kSecAttrAccessibleWhenUnlockedThisDeviceOnly
        // Both are deprecated since iOS >12
        val Always = kSecAttrAccessibleAlways
        val AlwaysThisDeviceOnly = kSecAttrAccessibleAlwaysThisDeviceOnly
    }
}