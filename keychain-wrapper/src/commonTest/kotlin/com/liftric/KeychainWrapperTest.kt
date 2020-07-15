package com.liftric

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

expect class KeychainWrapperTest : AbstractKeychainWrapperTest
abstract class AbstractKeychainWrapperTest(private val keychain: KeychainWrapper) {
    @Test
    fun testIfCheeseIsEqualToWine() {
        keychain.set(true, "cheese")
        val retValue = keychain.bool("cheese") ?: fail("cheese is not in the keychain")
        assertTrue(retValue, "the wrong cheese is in the keychain")
    }
}
