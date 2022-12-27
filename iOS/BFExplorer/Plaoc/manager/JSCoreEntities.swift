//
//  JSCoreHandle.swift
//  Plaoc-iOS
//
//  Created by ui03 on 2022/11/4.
//

import Foundation
import JavaScriptCore


@objc protocol PlaocJSExport: JSExport {
    
    func callJavaScript(functionName: String, param: Any) -> Any
    
}


@objc class PlaocHandleModel: NSObject, PlaocJSExport {
    
    var controller: WebViewViewController?
    var jsContext: JSContext?
    var fileName: String = ""
    
    func callJavaScript(functionName: String, param: Any) -> Any {
        print(functionName)
        switch functionName {
        case "OpenDWebView":
            return executiveOpenDWebView(param: param)
        case "OpenQrScanner":
            return executiveOpenQrScanner(param: param)
        case "BarcodeScanner":
            return executiveOpenBarcodeScanner(param: param)
        case "InitMetaData":
            return executiveInitMetaData(param: param)
        case "DenoRuntime":
            return executiveDenoRuntime(param: param)
        case "GetBfsAppId":
            return executiveGetBfsAppId(param: param)
        case "EvalJsRuntime":
            return executiveEvalJsRuntime(param: param)
        case "GetDeviceInfo":
            return executiveGetDeviceInfo(param: param)
        case "SendNotification":
            return executiveSendNotification(param: param)
        case "GetNotification":
            return executiveGetNotification(param: param)
        case "ApplyPermissions":
            return executiveApplyPermissions(param: param)
        case "isDenoRuntime":
            return executiveIsDenoRuntime(param: param)
        case "FileSystemLs":
            return executiveFileSystemLs(param: param)
        case "FileSystemMkdir":
            return executiveFileSystemMkdir(param: param)
        case "FileSystemRm":
            return executiveFileSystemRm(param: param)
        case "FileSystemRead":
            return executiveFileSystemRead(param: param)
        case "FileSystemWrite":
            return executiveFileSystemWrite(param: param)
        case "FileSystemStat":
            return executiveFileSystemStat(param: param)
        case "FileSystemList":
            return executiveFileSystemList(param: param)
        case "SetStatusBarColor":
            return updateStatusBarColor(param: param)
        case "GetStatusBarColor":
            return statusBarColor(param: param)
        case "GetStatusBarIsDark":
            return statusBarIsDark(param: param)
        case "GetStatusBarVisible":
            return statusBarVisible(param: param)
        case "SetStatusBarVisible":
            return updateStatusBarVisible(param: param)
        case "GetStatusBarOverlay":
            return statusBarOverlay(param: param)
        case "SetStatusBarOverlay":
            return updateStatusBarOverlay(param: param)
        case "GetKeyBoardSafeArea":
            return keyboardSafeArea(param: param)
        case "GetKeyBoardHeight":
            return keyboardHeight(param: param)
        case "GetKeyBoardOverlay":
            return keyboardOverlay(param: param)
        case "SetKeyBoardOverlay":
            return updateKeyboardOverlay(param: param)
        case "ShowKeyBoard":
            return showKeyboard(param: param)
        case "HideKeyBoard":
            return hideKeyBoard(param: param)
        case "TopBarNavigationBack":
            return topBarNavigationBack(param: param)
        case "GetTopBarShow":
            return topbarShow(param: param)
        case "SetTopBarShow":
            return updateTopBarShow(param: param)
        case "GetTopBarOverlay":
            return topBarOverlay(param: param)
        case "SetTopBarOverlay":
            return updateTopBarOverlay(param: param)
        case "GetTopBarAlpha":
            return topBarAlpha(param: param)
        case "SetTopBarAlpha":
            return updateTopBarAlpha(param: param)
        case "GetTopBarTitle":
            return topBarTitle(param: param)
        case "SetTopBarTitle":
            return updateTopBarTitle(param: param)
        case "HasTopBarTitle":
            return isTopBarTitle(param: param)
        case "GetTopBarHeight":
            return topBarHeight(param: param)
        case "GetTopBarActions":
            return topBarActions(param: param)
        case "SetTopBarActions":
            return updateTopBarActions(param: param)
        case "GetTopBarBackgroundColor":
            return topBarBackgroundColor(param: param)
        case "SetTopBarBackgroundColor":
            return updateTopBarBackgroundColor(param: param)
        case "GetTopBarForegroundColor":
            return topBarForegroundColor(param: param)
        case "SetTopBarForegroundColor":
            return updateTopBarForegroundColor(param: param)
        case "GetBottomBarEnabled":
            return bottomBarShow(param: param)
        case "SetBottomBarEnabled":
            return updateBottomBarShow(param: param)
        case "GetBottomBarAlpha":
            return bottomBarAlpha(param: param)
        case "SetBottomBarAlpha":
            return updateBottomBarAlpha(param: param)
        case "GetBottomBarHeight":
            return bottomBarHeight(param: param)
        case "SetBottomBarHeight":
            return updateBottomBarHeight(param: param)
        case "GetBottomBarActions":
            return bottomBarActions(param: param)
        case "SetBottomBarActions":
            return updateBottomBarActions(param: param)
        case "GetBottomBarBackgroundColor":
            return bottomBarBackgroundColor(param: param)
        case "SetBottomBarBackgroundColor":
            return updateBottomBarBackgroundColor(param: param)
        case "GetBottomBarForegroundColor":
            return bottomBarForegroundColor(param: param)
        case "SetBottomBarForegroundColor":
            return updatebottomBarForegroundColor(param: param)
        case "OpenDialogAlert":
            return OpenDialogAlert(param: param)
        case "OpenDialogPrompt":
            return OpenDialogPrompt(param: param)
        case "OpenDialogConfirm":
            return OpenDialogConfirm(param: param)
        case "OpenDialogWarning":
            return OpenDialogWarning(param: param)
        default:
            return ""
        }
    }
}


extension PlaocHandleModel {
    //打开DWebView
    private func executiveOpenDWebView(param: Any) -> Bool {
        NotificationCenter.default.post(name: NSNotification.Name.openDwebNotification, object: nil, userInfo: ["param":param])
        return true
    }
    //二维码
    private func executiveOpenQrScanner(param: Any) -> String {
        let scanVC = ScanPhotoViewController()
        controller?.navigationController?.pushViewController(scanVC, animated: true)
        return ""
    }
    //条形码
    private func executiveOpenBarcodeScanner(param: Any) -> String {
        let scanVC = ScanPhotoViewController()
        controller?.navigationController?.pushViewController(scanVC, animated: true)
        return ""
    }
    //初始化app数据
    private func executiveInitMetaData(param: Any) -> String {
        guard let param = param as? String else { return "" }
        NetworkMap.shared.metaData(metadata: param, fileName: fileName)
        return ""
    }
    //初始化运行时
    private func executiveDenoRuntime(param: Any) -> String {
        return ""
    }
    //获取appID
    private func executiveGetBfsAppId(param: Any) -> String {
        return ""
    }
    //传递给前端消息
    private func executiveEvalJsRuntime(param: Any) -> String {
//        return jsContext?.evaluateScript(param)
        return ""
    }
    //获取设备信息
    private func executiveGetDeviceInfo(param: Any) -> String {
        return ""
    }
    //发送消息  调用系统通知
    private func executiveSendNotification(param: Any) -> String {
        return ""
    }
    //获取推送消息列表
    private func executiveGetNotification(param: Any) -> String {
        return ""
    }
    //申请权限
    private func executiveApplyPermissions(param: Any) -> String {
        let permission = PermissionManager()
        permission.startPermissionAuthenticate(type: .bluetooth) { result in
            
        }
        return ""
    }
    //
    private func executiveIsDenoRuntime(param: Any) -> String {
        return "true"
    }
}

