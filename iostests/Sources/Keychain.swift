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
        KVault(serviceName: "com.liftric.default", accessGroup: nil, accessibility: KSecAttrAccessible.afterfirstunlock)
    }

    public static var scoped: KVault {
        KVault(serviceName: "com.liftric.test", accessGroup: nil, accessibility: KSecAttrAccessible.afterfirstunlock)
    }

    public static var global: KVault {
        KVault(serviceName: nil, accessGroup: nil, accessibility: KSecAttrAccessible.afterfirstunlock)
    }
}
