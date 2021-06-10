//
//  Keychain.swift
//  TestApp
//
//  Created by Ben John on 16.07.20.
//  Copyright © 2020 Liftric GmbH. All rights reserved.
//

import Foundation
import kvault

public struct Keychain {

    // MARK: - Keychain

    public static var `default`: KVault {
        KVault(serviceName: "com.liftric.default", accessGroup: nil)
    }

    public static var scoped: KVault {
        KVault(serviceName: "com.liftric.test", accessGroup: nil)
    }

    public static var global: KVault {
        KVault()
    }
}
