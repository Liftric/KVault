package core

import platform.CoreFoundation.CFStringRef

enum class KeychainItemAccessibility(val attribute: CFStringRef?) {
    /**
     * The data in the keychain item cannot be accessed after a restart until the device has been unlocked once by the user.
     */
    afterFirstUnlock(Constants.Accessible.AfterFirstUnlock),
    /**
     * The data in the keychain item cannot be accessed after a restart until the device has been unlocked once by the user.
     */
    afterFirstUnlockThisDeviceOnly(Constants.Accessible.AfterFirstUnlockThisDeviceOnly),
    /**
     * The data in the keychain item can always be accessed regardless of whether the device is locked.
     */
    always(Constants.Accessible.Always),
    /**
     * The data in the keychain item can always be accessed regardless of whether the device is locked.
     */
    alwaysThisDeviceOnly(Constants.Accessible.AlwaysThisDeviceOnly),
    /**
     * The data in the keychain can only be accessed when the device is unlocked. Only available if a passcode is set on the device.
     */
    whenPasscodeSetThisDeviceOnly(Constants.Accessible.WhenPasscodeSetThisDeviceOnly),
    /**
     * The data in the keychain item can be accessed only while the device is unlocked by the user.
     */
    whenUnlocked(Constants.Accessible.WhenUnlocked),
    /**
     * The data in the keychain item can be accessed only while the device is unlocked by the user.
     */
    whenUnlockedThisDeviceOnly(Constants.Accessible.WhenUnlockedThisDeviceOnly)
}