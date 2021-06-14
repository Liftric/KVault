//
//  SceneDelegate.swift
//  TestApp
//
//  Created by Jan Gaebel on 22.05.21.
//  Copyright © 2020 Liftric GmbH. All rights reserved.
//

import UIKit

class SceneDelegate: UIResponder, UIWindowSceneDelegate {

    var window: UIWindow?

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        if let windowScene = scene as? UIWindowScene {
            let window = UIWindow(windowScene: windowScene)
            window.rootViewController = UINavigationController(rootViewController: DebugViewController())
            self.window = window
            window.makeKeyAndVisible()
        }
    }
}

