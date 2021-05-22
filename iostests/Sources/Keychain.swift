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

    // MARK: - KVault

    public static var defaultStore: KVault {
        KVault.Default()
    }

    public static var store: KVault {
        KVault(serviceName: "com.liftric.Test", accessGroup: nil)
    }
}
