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
        KVaultImpl(serviceName: "com.liftric.default", accessGroup: nil, accessibility: KVaultImpl.Accessible.afterfirstunlock)
    }

    public static var scoped: KVault {
        KVaultImpl(serviceName: "com.liftric.test", accessGroup: nil, accessibility: KVaultImpl.Accessible.afterfirstunlock)
    }

    public static var global: KVault {
        KVaultImpl(serviceName: nil, accessGroup: nil, accessibility: KVaultImpl.Accessible.afterfirstunlock)
    }
}
