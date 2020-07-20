//
//  KeychainWrapper2.swift
//  KryptaApp
//
//  Created by Ben John on 16.07.20.
//  Copyright © 2020 Liftric GmbH. All rights reserved.
//

import Foundation
import keychain_wrapper

public struct Keeper {
    
    // MARK: - Keeper

    public static var store: KeychainWrapper {
        let identifier = "com.liftric.Keeper"
        return KeychainWrapper(serviceName: identifier, accessGroup: nil)
    }
}
