//
//  StatusBarEntites.swift
//  BFExplorer
//
//  Created by ui03 on 2022/12/23.
//

import UIKit

extension PlaocHandleModel {

    //设置状态栏颜色
    func updateStatusBarColor(param: String) -> String {
        return ""
    }
    //获取状态栏颜色
    func statusBarColor(param: String) -> String {
        return ""
    }
    //状态栏是否是暗黑模式
    func statusBarIsDark(param: String) -> Bool {
        return false
    }
    //状态栏是否可见
    func statusBarVisible(param: String) -> Bool {
        return true
    }
    //设置状态栏是否可见
    func updateStatusBarVisible(param: String) -> String {
        return ""
    }
    //状态栏是否overlay
    func statusBarOverlay(param: String) -> Bool {
        return true
    }
    //设置状态栏overlay
    func updateStatusBarOverlay(param: String) -> String {
        return ""
    }
}
