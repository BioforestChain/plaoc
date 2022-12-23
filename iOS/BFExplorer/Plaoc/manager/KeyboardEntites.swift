//
//  KeyboardEntites.swift
//  BFExplorer
//
//  Created by ui03 on 2022/12/23.
//

import Foundation

extension PlaocHandleModel {
    //键盘安全区域
    func keyboardSafeArea(param: String) -> CGRect {
        return CGRect.zero
    }
    //键盘高度
    func keyboardHeight(param: String) -> CGFloat {
        return 0
    }
    //显示键盘
    func showKeyboard(param: String) -> Bool {
        return true
    }
    //隐藏键盘
    func hideKeyBoard(param: String) -> Bool {
        return true
    }
    //键盘是否overlay
    func keyboardOverlay(param: String) -> Bool {
        return true
    }
    //设置键盘overlay
    func updateKeyboardOverlay(param: String) -> String {
        return ""
    }
}
