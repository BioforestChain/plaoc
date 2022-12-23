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
    private var plaoc = PlaocHandleModel()
    private var name: String = ""
    
    
    init(fileName: String, controller: UIViewController?) {
        super.init()
        baseViewController = controller
        name = fileName
        
        plaoc.controller = baseViewController
        plaoc.jsContext = jsContext
        plaoc.fileName = name
        
        JSInjectManager.shared.registerInContext(jsContext!)
        initJSCore()
        loadAPPEntry(fileName: fileName)

    }
    
    private func initJSCore() {

        let entryPath = Bundle.main.bundlePath + "/sdk/HE74YAAL/boot/index.js"
        
        jsContext?.setObject(plaoc, forKeyedSubscript: "PlaocJavascriptBridge" as NSCopying & NSObjectProtocol)
        if let content = try? String(contentsOfFile: entryPath) {
            jsContext?.evaluateScript(content)
        }
    }
    
    private func loadAPPEntry(fileName: String) {
        
        guard let entryPath = BatchFileManager.shared.systemAPPEntryPath(fileName: fileName) else { return }
        
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
    
    func handleEvaluateScript(jsString: String) {
        let functionString = "webView.dwebviewToDeno" + "(\(jsString)"
        let result = jsContext?.evaluateScript(functionString)
        print(result?.toString())
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
