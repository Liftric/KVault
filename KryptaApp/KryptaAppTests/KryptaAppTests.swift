//
//  KryptaAppTests.swift
//  KryptaAppTests
//
//  Created by Ben John on 16.07.20.
//  Copyright © 2020 Liftric GmbH. All rights reserved.
//

import XCTest
@testable import KryptaApp

class KryptaAppTests: XCTestCase {

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testExample() throws {
        let keychain = KeychainWrapper2.keychain
        keychain.set(value: "hello", forKey_____: "world")
        XCTAssertEqual("hello", keychain.string(forKey: "world"))
    }

    func testPerformanceExample() throws {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }

}
