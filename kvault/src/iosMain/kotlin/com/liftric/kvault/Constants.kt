package com.liftric.kvault

import platform.Foundation.*

internal object Constants {
    private const val defaultIdentifier = "com.liftric.KVault"
    val BundleIdentifier = NSBundle.mainBundle.bundleIdentifier?: defaultIdentifier
}
