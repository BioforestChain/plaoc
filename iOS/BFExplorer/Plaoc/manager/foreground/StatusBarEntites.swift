//
//  StatusBarEntites.swift
//  BFExplorer
//
//  Created by ui03 on 2022/12/23.
//

import UIKit

extension PlaocHandleModel {

    //设置状态栏颜色
    func updateStatusBarColor(param: Any) -> Bool {
        guard let param = param as? String else { return false }
        controller?.updateStatusBackgroundColor(colorString: param)
        return true
    }
    //获取状态栏颜色
    func statusBarColor(param: Any) -> String {
        return controller?.statusBackgroundColor() ?? ""
    }
    //状态栏是否是暗黑模式
    func statusBarIsDark(param: Any) -> Bool {
        return controller?.statusBarStyle() == "true"
    }
    //状态栏是否可见
    func statusBarVisible(param: Any) -> Bool {
        return controller?.statusBarVisible() ?? false
    }
    //设置状态栏是否可见
    func updateStatusBarVisible(param: Any) -> Bool {
        guard let param = param as? Bool else { return false }
        controller?.updateStatusHidden(isHidden: param)
        return true
    }
    //状态栏是否overlay
    func statusBarOverlay(param: Any) -> Bool {
        return controller?.statusBarOverlay() ?? false
    }
    //设置状态栏overlay
    func updateStatusBarOverlay(param: Any) -> Bool {
        guard let param = param as? Bool else { return false }
        controller?.updateStatusBarOverlay(overlay: param)
        return true
    }
}
