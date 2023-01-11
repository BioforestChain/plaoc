//
//  JSCoreHandle.swift
//  Plaoc-iOS
//
//  Created by ui03 on 2022/11/4.
//

import UIKit
import Foundation
import JavaScriptCore
import SwiftyJSON


@objc protocol PlaocJSExport: JSExport {
    
    func callJavaScript(functionName: String, param: Any) -> Any
    
}


@objc class PlaocHandleModel: NSObject, PlaocJSExport {
    
    var controller: WebViewViewController?
    var jsContext: JSContext?
    var fileName: String = ""
    
    func callJavaScript(functionName: String, param: Any) -> Any {
        print("swift#callJavaScript:",functionName)
        switch functionName {
        case "OpenDWebView":
            return executiveOpenDWebView(param: param)
        case "OpenQrScanner":
            return executiveOpenQrScanner(param: param, functionName: functionName)
        case "BarcodeScanner":
            return executiveOpenBarcodeScanner(param: param, functionName: functionName)
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
            return executiveApplyPermissions(param: param, functionName: functionName)
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
        case "FileSystemRename":
            return executiveFileSystemRename(param: param)
        case "FileSystemReadBuffer":
            return executiveFileSystemReadBuffer(param: param)
        case "GetNetworkStatus":
            return ReachabilityManager.shared.getNetworkStatus()
        case "HapticsImpactLight":
            return FeedbackGenerator.impactFeedbackGenerator(style: .light)
        case "HapticsNotificationWarning":
            return FeedbackGenerator.notificationFeedbackGenerator(style: .warning)
        case "HapticsVibrate":
            return hapticsVibrate(param: param)
        case "ShowToast":
            return showToast(param: param)
        case "SystemShare":
            return systemShare(param: param)
        case "ReadClipboardContent":
            return readClipboardContent(param: param)
        case "WriteClipboardContent":
            return writeClipboardContent(param: param)
        // 前端ui
        case "SetDWebViewUI":
            return executiveDwebviewUI(param: param)
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
    private func executiveOpenQrScanner(param: Any, functionName: String) -> String {
        let scanVC = ScanPhotoViewController()
        scanVC.callback = { result in
            self.asyncReturnValue(functionName: functionName, result: result)
        }
        controller?.navigationController?.pushViewController(scanVC, animated: true)
        return ""
    }
    //条形码
    private func executiveOpenBarcodeScanner(param: Any, functionName: String) -> String {
        let scanVC = ScanPhotoViewController()
        scanVC.callback = { result in
            self.asyncReturnValue(functionName: functionName, result: result)
        }
        controller?.navigationController?.pushViewController(scanVC, animated: true)
        return ""
    }
    //初始化app数据
    private func executiveInitMetaData(param: Any) -> Void {
        guard let param = param as? String else { return }
        NetworkMap.shared.metaData(metadata: param, fileName: fileName)
    }
    //初始化运行时
    private func executiveDenoRuntime(param: Any) -> String {
        return ""
    }
    //获取appID
    private func executiveGetBfsAppId(param: Any) -> String {
        return fileName
    }
    //传递给前端消息
    private func executiveEvalJsRuntime(param: Any) -> String {
        guard let param = param as? String else { return "false" }
        controller?.evaluateJavaScript(jsString: param)
        
        return "true"
    }
    //获取设备信息
    private func executiveGetDeviceInfo(param: Any) -> String {
//        UIDevice.current.
        var dict : [String: Any] = [:]
        
        dict["deviceMode"] = UIDevice.current.device_model
//        dict["screen"] = UIDevice.current.resolution()
        dict["storage"] = UIDevice.current.totalMemorySize
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
    
    // 异步返回结果
    private func asyncReturnValue(functionName: String, result: Any) {
        self.jsContext?.evaluateScript("callDwebViewFactory('\(functionName)', '\(result)')")
    }
    
    //申请权限
    private func executiveApplyPermissions(param: Any, functionName: String) -> Bool {
        
        guard let param = param as? String else {
            asyncReturnValue(functionName: functionName, result: "false")
            return false
        }
        let permission = PermissionManager()
        let permissionType = PermissionManager.PermissionsType(rawValue: param)
        
        if permissionType == nil {
            asyncReturnValue(functionName: functionName, result: "false")
            return false
        }
        
        permission.startPermissionAuthenticate(type: permissionType!) { result in
            self.asyncReturnValue(functionName: functionName, result: result)
        }
        
        return true
    }
    //
    private func executiveIsDenoRuntime(param: Any) -> String {
        return "false"
    }
    
    private func hapticsVibrate(param: Any) -> Void {
        guard let param = param as? String, Float(param) != nil else { return }
        FeedbackGenerator.vibrate(Double(Float(param)!))
    }
    
    // 显示提示
    private func showToast(param: Any) {
        guard let param = param as? String else { return }
        let data = JSON.init(parseJSON: param)
        
        if controller != nil {
            let position = data["position"].exists() ? data["position"].stringValue : "bottom"
            let durationStr = data["duration"].exists() ? data["duration"].stringValue : "short"
            
            // short: 2000
            // long:  3500
            var duration = 2000
            if durationStr == "long" {
                duration = 3500
            }
            
            ToastManager.showToast(in: controller!, text: data["text"].stringValue, duration: duration, position: ToastManager.Position(rawValue: position) ?? ToastManager.Position.bottom)
        }
    }
    
    // 系统分享
    private func systemShare(param: Any) {
        guard let param = param as? String else { return }
        let data = JSON.init(parseJSON: param)
        var files: [String]?
        
        if data["files"].arrayValue is Array<String>, data["files"].count > 0 {
            files = data["files"].arrayValue as? [String]
        }
        
        ShareManager.loadSystemShare(title: data["title"].stringValue, text: data["text"].stringValue, url: data["url"].stringValue, files: files)
    }
    
    // 读取剪切板
    private func readClipboardContent(param: Any) -> String {
        let param = param as? String ?? ""
        let type = ClipboardManager.ContentType(rawValue: param) ?? ClipboardManager.ContentType.string
        let dict = ClipboardManager.read()
        
        return ChangeTools.dicValueString(dict) ?? ""
    }
    
    // 写入剪切板
    private func writeClipboardContent(param: Any) -> Bool {
        guard let param = param as? String else { return false }
        let data = JSON(parseJSON: param)
        let result: Result<Void, Error>
        
        if data["str"].exists() {
            result = ClipboardManager.write(content: data["str"].stringValue, ofType: ClipboardManager.ContentType.string)
        } else if (data["image"].exists()) {
            result = ClipboardManager.write(content: data["image"], ofType: ClipboardManager.ContentType.image)
        } else if (data["url"].exists()) {
            result = ClipboardManager.write(content: data["url"], ofType: ClipboardManager.ContentType.url)
        } else if (data["color"].exists()) {
            result = ClipboardManager.write(content: data["color"], ofType: ClipboardManager.ContentType.color)
        } else {
            result = .failure(ClipboardManager.ClipboardError.invalidType)
        }
        
        switch(result) {
        case .success():
            return true
        case .failure(let error):
            print("writeClipboardContent error: \(error.localizedDescription)")
            return false
        }
    }
}

