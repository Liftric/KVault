package com.liftric.kvault

import platform.Foundation.*

object Constants {
    private const val defaultIdentifier = "com.liftric.KVault"
    val BundleIdentifier = NSBundle.mainBundle.bundleIdentifier?: defaultIdentifier
}
