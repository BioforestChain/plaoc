//
//  JSCoreHandle.swift
//  Plaoc-iOS
//
//  Created by ui03 on 2022/11/4.
//

import Foundation
import JavaScriptCore

class JSCoreManager: NSObject {
    
    private var baseViewController: UIViewController?
    private let jsContext = JSContext()
    private var name: String = ""
    
    
    init(fileName: String, controller: UIViewController?) {
        super.init()
        baseViewController = controller
        name = fileName
        
        JSInjectManager.shared.registerInContext(jsContext!)
        initJSCore()
        loadAPPEntry(fileName: fileName)

    }
    
    private func initJSCore() {
//        let entryPath = "/Users/ui03/Desktop/Plaoc_IOS新/iOS/Plaoc-iOS/sys/HE74YAAL/sys/backend-HE74YAAL/index.js"
        let entryPath = Bundle.main.bundlePath + "/sys/HE74YAAL/sys/backend-HE74YAAL/index.js"
        let plaoc = PlaocHandleModel()
        plaoc.controller = baseViewController
        plaoc.jsContext = jsContext
        plaoc.fileName = name
        
        jsContext?.setObject(plaoc, forKeyedSubscript: "PlaocJavascriptBridge" as NSCopying & NSObjectProtocol)
        if let content = try? String(contentsOfFile: entryPath) {
            jsContext?.evaluateScript("(async function(){\(content)})()")
        }
    }
    
    private func loadAPPEntry(fileName: String) {
        
        guard let entryPath = BatchFileManager.shared.systemAPPEntryPath(fileName: fileName) else { return }

        let plaoc = PlaocHandleModel()
        plaoc.controller = baseViewController
        plaoc.jsContext = jsContext
        
        jsContext?.setObject(plaoc, forKeyedSubscript: "PlaocJavascriptBridge" as NSCopying & NSObjectProtocol)
        if let content = try? String(contentsOfFile: entryPath) {
            jsContext?.evaluateScript("(async function(){\(content)})()")
        }
    }
    
    func callFunction<T>(functionName: String, withData dataObject: Codable, type: T.Type) -> JSValue? where T:Codable {
        var dataString = ""
        if let string = getString(fromObject: dataObject, type: type) {
            dataString = string
        }
        let functionString = functionName + "(\(dataString)"
        let result = jsContext?.evaluateScript(functionString)
        return result
    }
}

extension JSCoreManager {
    
    private func getString<T>(fromObject jsonObject: Codable, type: T.Type) -> String? where T:Codable {
        let encoder = JSONEncoder()
        guard let dataObj = jsonObject as? T,
              let data = try? encoder.encode(dataObj),
              let string = String(data: data, encoding: .utf8) else { return nil }
        return string
    }
}
