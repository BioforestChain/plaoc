//
//  AppDelegate.swift
//  Plaoc-iOS
//
//  Created by mac on 2022/6/14.
//

import UIKit

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.rootViewController = CustomNaviViewController(rootViewController: ViewController())
        window?.makeKeyAndVisible()
        return true
    }




}

