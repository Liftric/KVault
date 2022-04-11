//
//  DebugViewController.swift
//  TestApp
//
//  Created by Jan Gaebel on 11.06.21.
//

import UIKit
import kvault

class DebugViewController: UIViewController {

    let button  = UIButton()

    override func viewDidLoad() {
        super.viewDidLoad()

        title = "test"
        view.backgroundColor = .white
        view.addSubview(button)

        button.translatesAutoresizingMaskIntoConstraints = false
        button.centerYAnchor.constraint(equalTo: view.centerYAnchor).isActive = true
        button.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        button.setTitle("DEBUG", for: .normal)
        button.setTitleColor(.red, for: .normal)

        button.addTarget(self, action: #selector(debug), for: .touchUpInside)
    }

    @objc func debug() {
        let vault = KVault(serviceName: "DEBUG", accessGroup: nil, accessibility: KSecAttrAccessible.afterfirstunlock)
        self.performOperations(on: vault)
    }

    private func performOperations(on vault: KVault) {
        vault.set(key: "A", boolValue: true)
        vault.set(key: "B", intValue: 1)
        vault.set(key: "C", floatValue: 1)
        vault.set(key: "D", doubleValue: 1)
        vault.set(key: "E", longValue: 1)
        vault.set(key: "F", stringValue: "T")
        _ = vault.existsObject(forKey: "A")
        _ = vault.bool(forKey: "A")
        _ = vault.int(forKey: "B")
        _ = vault.float(forKey: "C")
        _ = vault.double(forKey: "D")
        _ = vault.long(forKey: "E")
        vault.deleteObject(forKey: "F")
        vault.clear()
    }
}
