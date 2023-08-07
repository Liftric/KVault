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
        KVaultKeychain(serviceName: "com.liftric.default", accessGroup: nil, accessibility: KVaultKeychain.Accessible.afterfirstunlock)
    }

    public static var scoped: KVault {
        KVaultKeychain(serviceName: "com.liftric.test", accessGroup: nil, accessibility: KVaultKeychain.Accessible.afterfirstunlock)
    }

    public static var global: KVault {
        KVaultKeychain(serviceName: nil, accessGroup: nil, accessibility: KVaultKeychain.Accessible.afterfirstunlock)
    }
}
