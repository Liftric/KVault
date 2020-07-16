//
//  KeychainWrapper2.swift
//  KryptaApp
//
//  Created by Ben John on 16.07.20.
//  Copyright © 2020 Liftric GmbH. All rights reserved.
//

import Foundation
import keychain_wrapper

public struct KeychainWrapper2 {
    public static let keychain = KeychainWrapper.init(serviceName: "com.liftric.KryptaApp", accessGroup: nil)
}
