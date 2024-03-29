//
//  KeychainTests.swift
//  Tests
//
//  Created by Ben John on 16.07.20.
//  Copyright © 2020 Liftric GmbH. All rights reserved.
//

import XCTest
@testable import TestApp

class KeychainTests: XCTestCase {

    // MARK: - KeychainTests

    let sut = Keychain.default

    private func funnelAssertion<T: Equatable>(keys: [String], value: (String) -> T?) {
        let odd = keys.count % 2 > 0
        let middle = (keys.count - 1) / 2
        for (index, key) in keys.enumerated() {
            if (odd && index == middle) { continue }
            let otherKey = keys.reversed()[index]
            XCTAssertNotEqual(otherKey, key, "\(otherKey) should be different to \(key)")
            XCTAssertNotEqual(value(otherKey), value(key), "\(otherKey) should not yield to \(key)")
        }
    }

    func testSetGetString() throws {
        let testData = [
            "string1": "lorem ipsum",
            "string2": "lorem ipsum, dolor sit atmet",
            "string3": "lorem ipsum, ipsum lorem",
        ]

        testData.forEach {
            sut.set(key: $0.key, stringValue: $0.value)
            XCTAssertNotNil(sut.string(forKey: $0.key), "\($0.key) should not be null")
            XCTAssertEqual($0.value, sut.string(forKey: $0.key), "\($0.key) should resolve \($0.value)")
            XCTAssertNotEqual(sut.string(forKey: $0.key),
                              "Some string not contained in testData",
                              "\($0.key) should not resolve some other example not contained in testData")
        }

        funnelAssertion(keys: testData.keys.map { $0 }) {
            sut.string(forKey: $0)
        }
    }

    func testSetGetInt() throws {
        let testData = [
            "int1": Int32.min,
            "int2": Int32.max,
            "int3": 42,
        ]

        testData.forEach {
            sut.set(key: $0.key, intValue: $0.value)
            XCTAssertNotNil(sut.int(forKey: $0.key)?.int32Value, "\($0.key) should not be null")
            XCTAssertEqual($0.value, sut.int(forKey: $0.key)?.int32Value, "\($0.key) should resolve \($0.value)")
            XCTAssertNotEqual(sut.int(forKey: $0.key)?.int32Value, 1337,
                              "\($0.key) should not resolve some other example not contained in testData")
        }

        funnelAssertion(keys: testData.keys.map { $0 }) {
            sut.int(forKey: $0)?.int32Value
        }
    }

    func testSetGetLong() throws {
        let testData = [
            "long1": Int64.min,
            "long2": Int64.max,
            "long3": Int64(Int32.max) + 1,
        ]

        testData.forEach {
            sut.set(key: $0.key, longValue: $0.value)
            XCTAssertNotNil(sut.long(forKey: $0.key)?.int64Value, "\($0.key) should not be null")
            XCTAssertEqual($0.value, sut.long(forKey: $0.key)?.int64Value, "\($0.key) should resolve \($0.value)")
            XCTAssertNotEqual(sut.long(forKey: $0.key)?.int64Value, 1337,
                              "\($0.key) should not resolve some other example not contained in testData")
        }

        funnelAssertion(keys: testData.keys.map { $0 }) {
            sut.long(forKey: $0)?.int64Value
        }
    }

    func testSetGetFloat() throws {
        let testData = [
            "float1": Float.leastNormalMagnitude,
            "float2": Float.greatestFiniteMagnitude,
            "float3": 1337.1337,
        ]

        testData.forEach {
            sut.set(key: $0.key, floatValue: $0.value)
            XCTAssertNotNil(sut.float(forKey: $0.key)?.floatValue, "\($0.key) should not be null")
            XCTAssertEqual($0.value, sut.float(forKey: $0.key)?.floatValue, "\($0.key) should resolve \($0.value)")
            XCTAssertNotEqual(sut.float(forKey: $0.key)?.floatValue, 31337.31337,
                              "\($0.key) should not resolve some other example not contained in testData")
        }

        funnelAssertion(keys: testData.keys.map { $0 }) {
            sut.float(forKey: $0)?.floatValue
        }
    }

    func testSetGetDouble() throws {
        let testData = [
            "double1": Double.leastNormalMagnitude,
            "double2": Double.greatestFiniteMagnitude,
            "double3": 1337.1337,
        ]

        testData.forEach {
            sut.set(key: $0.key, doubleValue: $0.value)
            XCTAssertNotNil(sut.double(forKey: $0.key)?.doubleValue, "\($0.key) should not be null")
            XCTAssertEqual($0.value, sut.double(forKey: $0.key)?.doubleValue, "\($0.key) should resolve \($0.value)")
            XCTAssertNotEqual(sut.double(forKey: $0.key)?.doubleValue, 31337.31337,
                              "\($0.key) should not resolve some other example not contained in testData")
        }

        funnelAssertion(keys: testData.keys.map { $0 }) {
            sut.double(forKey: $0)?.doubleValue
        }
    }

    func testSetGetBool() throws {
        let testData = [
            "bool1": true,
            "bool2": false,
        ]

        testData.forEach {
            sut.set(key: $0.key, boolValue: $0.value)
            XCTAssertNotNil(sut.bool(forKey: $0.key)?.boolValue, "\($0.key) should not be null")
            XCTAssertEqual($0.value, sut.bool(forKey: $0.key)?.boolValue, "\($0.key) should resolve \($0.value)")
        }

        funnelAssertion(keys: testData.keys.map { $0 }) {
            sut.bool(forKey: $0)?.boolValue
        }
    }

    func testSetGetAll() throws {
        let boolData = ("bool", true)
        sut.set(key: boolData.0, boolValue: boolData.1)

        let doubleData = ("double", 0.0)
        sut.set(key: doubleData.0, doubleValue: doubleData.1)

        let longData = ("long", 1)
        sut.set(key: longData.0, longValue: Int64(longData.1))

        let allKeys = sut.allKeys()
        XCTAssertEqual(allKeys.count, 3)
        XCTAssertEqual(allKeys[0], boolData.0)
        XCTAssertEqual(allKeys[1], doubleData.0)
        XCTAssertEqual(allKeys[2], longData.0)
    }

    func testExistsObject() {
        XCTAssertFalse(sut.existsObject(forKey: "blank"), "Blank should not exist")
        sut.set(key: "blank", stringValue: "124")
        sut.set(key: "bla", intValue: 1)
        XCTAssertTrue(sut.existsObject(forKey: "blank"), "Blank should exist")
    }

    func testDeleteObject() {
        XCTAssertFalse(sut.existsObject(forKey: "blank"), "Blank should not exist")
        sut.set(key: "blank", stringValue: "123")
        XCTAssertTrue(sut.existsObject(forKey: "blank"), "Blank should exist")
        sut.deleteObject(forKey: "blank")
        XCTAssertFalse(sut.existsObject(forKey: "blank"), "Blank should not exist anymore")
    }

    func testOverwrite() {
        sut.set(key: "keyX", stringValue: "dummyX")
        XCTAssertEqual("dummyX", sut.string(forKey: "keyX"))
        sut.set(key: "keyX", stringValue: "dummyXY")
        XCTAssertEqual("dummyXY", sut.string(forKey: "keyX"))
        XCTAssertNotEqual("dummyX", sut.string(forKey: "keyX"))
    }

    func testClear() {
        let keys = ["key1", "key2", "key3", "key4", "key5"]

        keys.forEach {
            sut.set(key: $0, stringValue: "dummy")
            XCTAssertTrue(sut.existsObject(forKey: $0), "\($0) should exist")
        }

        sut.clear()

        keys.forEach {
            XCTAssertFalse(sut.existsObject(forKey: $0), "\($0) should not exist anymore")
        }
    }

    func testClearOnlyInOwnScope() {
        let keys = ["key1", "key2", "key3"]

        let sut2 = Keychain.scoped

        [sut, sut2].forEach { sut in
            keys.forEach {
                sut.set(key: $0, stringValue: "dummy")
                XCTAssertTrue(sut.existsObject(forKey: $0), "\($0) should exist")
            }
        }

        sut2.clear()

        keys.forEach {
            XCTAssertTrue(sut.existsObject(forKey: $0), "\($0) should exist")
        }

        keys.forEach {
            XCTAssertFalse(sut2.existsObject(forKey: $0), "\($0) should not exist anymore")
        }

        keys.forEach {
            sut2.set(key: $0, stringValue: "dummy")
            XCTAssertTrue(sut2.existsObject(forKey: $0), "\($0) should exist")
        }

        sut.clear()

        keys.forEach {
            XCTAssertTrue(sut2.existsObject(forKey: $0), "\($0) should exist")
        }

        keys.forEach {
            XCTAssertFalse(sut.existsObject(forKey: $0), "\($0) should not exist anymore")
        }
    }

    func testClearGlobally() {
        let keys = ["key1", "key2", "key3"]

        let sut2 = Keychain.global

        [sut, sut2].forEach { sut in
            keys.forEach {
                sut.set(key: $0, stringValue: "dummy")
                XCTAssertTrue(sut.existsObject(forKey: $0), "\($0) should exist")
            }
        }

        sut2.clear()

        keys.forEach {
            XCTAssertFalse(sut.existsObject(forKey: $0), "\($0) should not exist anymore")
        }

        keys.forEach {
            XCTAssertFalse(sut2.existsObject(forKey: $0), "\($0) should not exist anymore")
        }
    }

    override func tearDown() {
        Keychain.global.clear()
        super.tearDown()
    }
}
