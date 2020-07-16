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
    fun testSetGetString() {
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
    fun testSetGetInt() {
        val testdata = mapOf(
            "int1" to Int.MIN_VALUE,
            "int2" to Int.MAX_VALUE,
            "int3" to 1337
        )
        testdata.values.toTypedArray().last()
        testdata.forEach {
            keychain.set(it.value, it.key)
            assertNull(keychain.string(it.key), "${it.key} should be null as it is an Int not a String")
            assertNotNull(keychain.int(it.key), "${it.key} should not be null")
            assertEquals(it.value, keychain.int(it.key), "${it.key} should resolve ${it.value}")
            assertNotEquals(
                1,
                keychain.int(it.key),
                "${it.key} should not resolve some other example not contained in testdata"
            )
        }
        funnelAssertion<Int>(testdata.keys.toList()) {
            keychain.int(it)
        }
    }

    @Test
    fun testSetGetLong() {
        val testdata = mapOf(
            "long1" to Long.MIN_VALUE,
            "long2" to Long.MAX_VALUE,
            "long3" to Int.MAX_VALUE.toLong() + 1
        )
        testdata.values.toTypedArray().last()
        testdata.forEach {
            keychain.set(it.value, it.key)
            assertNull(keychain.string(it.key), "${it.key} should be null as it is an Int not a String")
            assertNotNull(keychain.long(it.key), "${it.key} should not be null")
            assertEquals(it.value, keychain.long(it.key), "${it.key} should resolve ${it.value}")
            assertNotEquals(
                1,
                keychain.long(it.key),
                "${it.key} should not resolve some other example not contained in testdata"
            )
        }
        funnelAssertion<Long>(testdata.keys.toList()) {
            keychain.long(it)
        }
    }

    @Test
    fun testSetGetFloat() {
        val testdata = mapOf(
            "long1" to Float.MIN_VALUE,
            "long2" to Float.MAX_VALUE,
            "long3" to 1337.1337f
        )
        testdata.values.toTypedArray().last()
        testdata.forEach {
            keychain.set(it.value, it.key)
            assertNull(keychain.string(it.key), "${it.key} should be null as it is an Int not a String")
            assertNotNull(keychain.float(it.key), "${it.key} should not be null")
            assertEquals(it.value, keychain.float(it.key), "${it.key} should resolve ${it.value}")
            assertNotEquals(
                31337.31337f,
                keychain.float(it.key),
                "${it.key} should not resolve some other example not contained in testdata"
            )
        }
        funnelAssertion<Float>(testdata.keys.toList()) {
            keychain.float(it)
        }
    }

    @Test
    fun testSetGetDouble() {
        val testdata = mapOf(
            "double1" to Double.MIN_VALUE,
            "double2" to Double.MAX_VALUE,
            "double3" to 1337.1337
        )
        testdata.values.toTypedArray().last()
        testdata.forEach {
            keychain.set(it.value, it.key)
            assertNull(keychain.string(it.key), "${it.key} should be null as it is an Int not a String")
            assertNotNull(keychain.double(it.key), "${it.key} should not be null")
            assertEquals(it.value, keychain.double(it.key), "${it.key} should resolve ${it.value}")
            assertNotEquals(
                31337.31337,
                keychain.double(it.key),
                "${it.key} should not resolve some other example not contained in testdata"
            )
        }
        funnelAssertion<Double>(testdata.keys.toList()) {
            keychain.double(it)
        }
    }

    @Test
    fun testSetGetBoolean() {
        val testdata = mapOf(
            "boolean1" to true,
            "boolean2" to false
        )
        testdata.values.toTypedArray().last()
        testdata.forEach {
            keychain.set(it.value, it.key)
            assertNull(keychain.string(it.key), "${it.key} should be null as it is an Int not a String")
            assertNotNull(keychain.bool(it.key), "${it.key} should not be null")
            assertEquals(it.value, keychain.bool(it.key), "${it.key} should resolve ${it.value}")
        }
        funnelAssertion<Boolean>(testdata.keys.toList()) {
            keychain.bool(it)
        }
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
