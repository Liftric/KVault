package com.liftric.kvault

import platform.CoreFoundation.CFStringRef
import platform.Security.*

/**
 * kSecAttrAccessible attributes wrapper.
 * attribute enables you to control item availability relative to the lock state of the device.
 * It also lets you specify eligibility for restoration to a new device.
 * If the attribute ends with the string ThisDeviceOnly, the item can be restored to the same device
 * that created a backup, but it isn’t migrated when restoring another device’s backup data.
 */
enum class KSecAttrAccessible(val value: CFStringRef?) {
    WhenPasscodeSetThisDeviceOnly(kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly),
    WhenUnlockedThisDeviceOnly(kSecAttrAccessibleWhenUnlockedThisDeviceOnly),
    WhenUnlocked(kSecAttrAccessibleWhenUnlocked),
    AfterFirstUnlock(kSecAttrAccessibleAfterFirstUnlock),
    AfterFirstUnlockThisDeviceOnly(kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly)
}