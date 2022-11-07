//
//  JSCoreHandle.swift
//  Plaoc-iOS
//
//  Created by ui03 on 2022/11/4.
//

import Foundation
import JavaScriptCore

@objc protocol PlaocJSExport: JSExport {
    
    func callJavaScriptWith(functionName: String, param: String) -> String
    
}


@objc class PlaocHandleModel: NSObject, PlaocJSExport {
    
    var controller: UIViewController?
    var jsContext: JSContext?
    
    func callJavaScriptWith(functionName: String, param: String) -> String {
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
        case "ApplyPermissions":
            return executiveApplyPermissions(param: param)
        default:
            return ""
        }
    }
}


extension PlaocHandleModel {
    //打开DWebView
    private func executiveOpenDWebView(param: String) -> String {
        return ""
    }
    //二维码
    private func executiveOpenQrScanner(param: String) -> String {
        let scanVC = ScanPhotoViewController()
        controller?.navigationController?.pushViewController(scanVC, animated: true)
        return ""
    }
    //条形码
    private func executiveOpenBarcodeScanner(param: String) -> String {
        let scanVC = ScanPhotoViewController()
        controller?.navigationController?.pushViewController(scanVC, animated: true)
        return ""
    }
    //初始化app数据
    private func executiveInitMetaData(param: String) -> String {
        return ""
    }
    //初始化运行时
    private func executiveDenoRuntime(param: String) -> String {
        return ""
    }
    //获取appID
    private func executiveGetBfsAppId(param: String) -> String {
        return ""
    }
    //传递给前端消息
    private func executiveEvalJsRuntime(param: String) -> String {
        return ""
    }
    //获取设备信息
    private func executiveGetDeviceInfo(param: String) -> String {
        return ""
    }
    //发送消息
    private func executiveSendNotification(param: String) -> String {
        return ""
    }
    //申请权限
    private func executiveApplyPermissions(param: String) -> String {
        return ""
    }
}
