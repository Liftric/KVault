package com.liftric

import kotlin.test.*

expect class KeychainWrapperTest : AbstractKeychainWrapperTest
abstract class AbstractKeychainWrapperTest(private val keychain: KeychainWrapper) {
    /**
     * Go to center from both sides and assume that keys are unique and values are always different.
     */
    private fun <T> funnelAssertion(keys: List<String>, value: (String) -> T?) {
        val odd = keys.size % 2 > 0
        val middle = (keys.size - 1) / 2
        for (pos in keys.indices) {
            if (odd && pos == middle) continue // skip middle object
            val key = keys[pos]
            val otherKey = keys.asReversed()[pos]
            assertNotEquals(otherKey, key, "$otherKey should be different to $key")
            assertNotEquals(value(otherKey), value(key), "$otherKey should not yield to $key")
        }
    }

    @Test
    fun testSetString() {
        val testdata = mapOf(
            "string1" to "lorem ipsum",
            "string2" to "lorem ipsum \r lorem ipsum",
            "string3" to "some other text"
        )
        testdata.values.toTypedArray().last()
        testdata.forEach {
            keychain.set(it.value, it.key)
            assertNotNull(keychain.string(it.key), "${it.key} should not be null")
            assertEquals(it.value, keychain.string(it.key), "${it.key} should resolve ${it.value}")
            assertNotEquals(
                "some other example not contained in testdata",
                keychain.string(it.key),
                "${it.key} should not resolve some other example not contained in testdata"
            )
        }

        funnelAssertion<String>(testdata.keys.toList()) {
            keychain.string(it)
        }
    }

    @Test
    fun testSetInt() {
        val testdata = mapOf(
            "int1" to 0,
            "int2" to 1,
            "int3" to 2
        )
        testdata.values.toTypedArray().last()
        testdata.forEach {
            keychain.set(it.value, it.key)
            assertNull(keychain.string(it.key), "${it.key} should be null as it is an Int not a String")
            assertNotNull(keychain.int(it.key), "${it.key} should not be null")
            assertEquals(it.value, keychain.int(it.key), "${it.key} should resolve ${it.value}")
            assertNotEquals(
                "some other example not contained in testdata",
                keychain.string(it.key),
                "${it.key} should not resolve some other example not contained in testdata"
            )
        }

        funnelAssertion<Int>(testdata.keys.toList()) {
            keychain.int(it)
        }
    }

    @Test
    fun testSetLong() {

    }

    @Test
    fun testSetFloat() {

    }

    @Test
    fun testSetDouble() {

    }

    @Test
    fun testSetBoolean() {

    }

    @Test
    fun testGetString() {
    }

    @Test
    fun testGetInt() {

    }

    @Test
    fun testGetLong() {

    }

    @Test
    fun testGetDouble() {
    }

    @Test
    fun testGetFloat() {

    }

    @Test
    fun testGetBool() {
    }

    @Test
    fun testExistsObject() {
    }

    @Test
    fun testDeleteObject() {
    }

    @Test
    fun testClear() {
    }
}
