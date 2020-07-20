//
//  KeychainWrapper2.swift
//  KryptaApp
//
//  Created by Ben John on 16.07.20.
//  Copyright © 2020 Liftric GmbH. All rights reserved.
//

import Foundation
import kvault

public struct Keychain {
    
    // MARK: - KVault

    public static var store: KVault {
        let identifier = "com.liftric.Keeper"
        return KVault(serviceName: identifier, accessGroup: nil)
    }
}
