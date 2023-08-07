//
//  DebugViewController.swift
//  TestApp
//
//  Created by Jan Gaebel on 11.06.21.
//

import UIKit
import kvault

class DebugViewController: UIViewController {
    private let keyA = "A"
    private let keyB = "B"
    private let keyC = "C"
    private let keyD = "D"
    private let keyE = "E"
    private let keyF = "F"
    
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
        let vault = KVaultKeychain(serviceName: "DEBUG", accessGroup: nil, accessibility: KVaultKeychain.Accessible.afterfirstunlock) as KVault
        self.performOperations(on: vault)
    }
    
    private func performOperations(on vault: KVault) {
        vault.set(key: keyA, boolValue: true)
        print("Value for \(keyA): \(vault.bool(forKey: keyA) ?? false)")
        
        vault.set(key: keyB, intValue: 1)
        print("Value for \(keyB): \(vault.int(forKey: keyB) ?? -1)")
        
        vault.set(key: keyC, floatValue: 1)
        print("Value for \(keyC): \(vault.float(forKey: keyC) ?? -1.0)")
        
        vault.set(key: keyD, doubleValue: 1)
        print("Value for \(keyD): \(vault.double(forKey: keyD) ?? -1.0)")
        
        vault.set(key: keyE, longValue: 1)
        print("Value for \(keyE): \(vault.long(forKey: keyE) ?? -1)")
        
        vault.set(key: keyF, stringValue: "T")
        print("Value for \(keyF): \(vault.string(forKey: keyF) ?? "NA")")
        
        _ = vault.existsObject(forKey: keyA)
        _ = vault.bool(forKey: keyA)
        _ = vault.int(forKey: keyB)
        _ = vault.float(forKey: keyC)
        _ = vault.double(forKey: keyD)
        _ = vault.long(forKey: keyE)
        vault.deleteObject(forKey: keyF)
        vault.clear()
    }
}
