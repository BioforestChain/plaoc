//
//  AppDelegate.swift
//  Plaoc-iOS
//
//  Created by mac on 2022/6/14.
//

import UIKit
import MagicalRecord

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.rootViewController = CustomNaviViewController(rootViewController: FirstViewController())
        
//        let str = NSString(string: Bundle.main.bundlePath)
//        let path = str.appendingPathComponent("resource_3rd/bmr9vohvtvbvwrs3p4bwgzsmolhtphsvvj/sys")
//        Schemehandler.setupHTMLCache(fromPath: path)
        
        window?.makeKeyAndVisible()
        
//        MagicalRecord.setupCoreDataStack(withAutoMigratingSqliteStoreNamed: "Plaoc")
        BatchTempManager().readTempMetadata(name: "KEJPMHLA")
        return true
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
//        MagicalRecord.cleanUp()
    }
    
}

