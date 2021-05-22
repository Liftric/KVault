//
//  Keychain.swift
//  TestApp
//
//  Created by Jan Gaebel on 22.05.21.
//

import Foundation
import kvault

public struct Keychain {

    // MARK: - KVault

    public static var defaultStore: KVault {
        KVault()
    }

    public static var store: KVault {
        KVault(serviceName: "com.liftric.Test", accessGroup: nil)
    }
}
