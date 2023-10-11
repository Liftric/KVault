package com.liftric.kvault

import kotlin.test.*

expect class KVaultTest: AbstractKVaultTest
abstract class AbstractKVaultTest(private val keychain: KVault) {
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
            keychain.set(it.key, it.value)
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
            keychain.set(it.key, it.value)
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
            keychain.set(it.key, it.value)
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
            keychain.set(it.key, it.value)
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
            keychain.set(it.key, it.value)
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
            keychain.set(it.key, it.value)
            assertNull(keychain.string(it.key), "${it.key} should be null as it is an Int not a String")
            assertNotNull(keychain.bool(it.key), "${it.key} should not be null")
            assertEquals(it.value, keychain.bool(it.key), "${it.key} should resolve ${it.value}")
        }
        funnelAssertion<Boolean>(testdata.keys.toList()) {
            keychain.bool(it)
        }
    }

    @Test
    fun testSetGetData() {
        val testData = mapOf(
            "data1" to "Hello World!".encodeToByteArray(),
            "data2" to "Foo bar".encodeToByteArray(),
            "data3" to "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.".encodeToByteArray()
        )
        testData.values.toTypedArray().last() // - Why this line?
        testData.forEach {
            keychain.set(it.key, it.value)
            assertContentEquals(it.value, keychain.data(it.key), "${it.key} should resolve to ${it.value.decodeToString()}")
        }
        funnelAssertion<ByteArray>(testData.keys.toList()) {
            keychain.data(it)
        }
    }

    @Test
    fun testSetGetAll() {
        val boolData = Pair("bool", true)
        keychain.set(boolData.first, boolData.second)

        val doubleData = Pair("double", Double.MIN_VALUE)
        keychain.set(doubleData.first, doubleData.second)

        val longData = Pair("long", Long.MAX_VALUE)
        keychain.set(longData.first, longData.second)

        val allKeys = keychain.allKeys()
        assertEquals(allKeys.size, 3)
        assertEquals(allKeys[0], boolData.first)
        assertEquals(allKeys[1], doubleData.first)
        assertEquals(allKeys[2], longData.first)
    }

    @Test
    fun testExistsObject() {
        assertFalse(keychain.existsObject("blank"), "blank should not exist")
        keychain.set("blank", "123")
        keychain.set("bla", 1)
        assertTrue(keychain.existsObject("blank"), "blank should exist")
    }

    @Test
    fun testDeleteObject() {
        assertFalse(keychain.existsObject("blank"), "blank should not exist")
        keychain.set("blank", "123")
        assertTrue(keychain.existsObject("blank"), "blank should exist")
        keychain.deleteObject("blank")
        assertFalse(keychain.existsObject("blank"), "blank should not exist after removal")
    }

    @Test
    fun testOverride() {
        keychain.set("keyX", "dummyX")
        assertEquals("dummyX", keychain.string("keyX"), "")
        keychain.set("keyX", "dummyX2")
        assertEquals("dummyX2", keychain.string("keyX"), "")
        assertNotEquals("dummyX", keychain.string("keyX"), "")
    }

    @Test
    fun testClear() {
        val keys = listOf("key1", "key2", "key3", "key4", "key5")
        keys.forEach {
            keychain.set(it, "dummy")
            assertTrue(keychain.existsObject(it), "$it should exist")
        }
        keychain.clear()
        keys.forEach {
            assertFalse(keychain.existsObject(it), "$it should not exist after clear")
        }
    }
}
