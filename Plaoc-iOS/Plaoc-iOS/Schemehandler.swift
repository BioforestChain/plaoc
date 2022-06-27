//
//  Schemehandler.swift
//  DWebBrowser
//
//  Created by mac on 2022/5/23.
//

import UIKit
import WebKit
import MobileCoreServices

let schemeString: String = "iosqmkkx"

class Schemehandler: NSObject, WKURLSchemeHandler {

    private var schemeTasksDict: [String: Bool] = [:]
    private var schemeTask: WKURLSchemeTask?
    private var typestring: String = ""
    
    func webView(_ webView: WKWebView, start urlSchemeTask: WKURLSchemeTask) {
        guard var urlstring = urlSchemeTask.request.url?.absoluteString else { return }
        schemeTasksDict[urlSchemeTask.description] = true
        guard let filename = urlSchemeTask.request.url?.path else { return }
//        let markstring = Schemehandler.fileName() + "/"
//        let range = urlstring.range(of: markstring)
//        if range != nil {
//            filename = String(urlstring[range!.lowerBound..<urlstring.endIndex])
//        }
        let mainPath = Schemehandler.filePath()
        let htmlPath = mainPath + "/www"
        let filepath = htmlPath + filename
        print(filepath)
        let manager = FileManager.default
        if manager.fileExists(atPath: filepath) {
            
            guard let data = manager.contents(atPath: filepath) else { return }
            var type = self.mimeType(pathExtension: filepath)
            if type.count == 0 {
                type = "text/html"
            }
            self.typestring = type
            
            let response = URLResponse(url: urlSchemeTask.request.url!, mimeType: type, expectedContentLength: data.count, textEncodingName: nil)
            urlSchemeTask.didReceive(response)
            
            //            print(Date().timeIntervalSince1970)
            //            let stream = InputStream(fileAtPath: filepath)
            //            stream!.open()
            //            let bufferSize = 1024
            //            let buffer = UnsafeMutablePointer<UInt8>.allocate(capacity: bufferSize)
            //            while stream!.hasBytesAvailable {
            //                let length = stream!.read(buffer, maxLength: bufferSize)
            //                let data = Data(bytes: buffer, count: length)
            //                urlSchemeTask.didReceive(data)
            //            }
            //            stream?.close()
            //            print(Date().timeIntervalSince1970)
            
            urlSchemeTask.didReceive(data)
            urlSchemeTask.didFinish()
            
            print("11111111")
            print(Date.timeIntervalSinceReferenceDate)
        } else {
            if urlstring.hasPrefix(schemeString) {
                urlstring = urlstring.replacingOccurrences(of: schemeString, with: "http")
            }
            guard let url = URL(string: urlstring) else { return }
            let request = URLRequest(url: url)
            let config = URLSessionConfiguration.default
            let session = URLSession(configuration: config)
            let task = session.dataTask(with: request) { data, response, error in
                DispatchQueue.main.async {
                    let isScheme = self.schemeTasksDict[urlSchemeTask.description] ?? false
                    guard isScheme else { return }
                    if (response != nil) {
                        urlSchemeTask.didReceive(response!)
                    } else {
                        let response = URLResponse(url: urlSchemeTask.request.url!, mimeType: "空类型", expectedContentLength: data?.count ?? 0, textEncodingName: nil)
                        urlSchemeTask.didReceive(response)
                    }
                    if data != nil {
                        urlSchemeTask.didReceive(data!)
                    }
                    if (error != nil) {
                        urlSchemeTask.didFailWithError(error!)
                    } else {
                        urlSchemeTask.didFinish()
                    }
                    
                    print("11111111")
                    print(Date.timeIntervalSinceReferenceDate)
                }
            }
            task.resume()
        }
    }

    func webView(_ webView: WKWebView, stop urlSchemeTask: WKURLSchemeTask) {
        print("stop")
        schemeTasksDict[urlSchemeTask.description] = false
    }
    
    static func setupHTMLCache(fromPath: String) {
        let manager = FileManager.default
        let markString = Schemehandler.fileName()
        let toPath = Schemehandler.filePath() + "/" + markString
        if manager.fileExists(atPath: toPath) {
            
        } else {
            clearHTMLCache()
            try? manager.copyItem(atPath: fromPath, toPath: toPath)
        }
    }
    
    static func clearHTMLCache() {
        let manager = FileManager.default
        let markString = fileName()
        let toPath = filePath() + "/" + markString
        if manager.fileExists(atPath: toPath) {
            try? manager.removeItem(atPath: toPath)
        }
    }
    
    static func fileName() -> String {
        return "www"
    }

    static func filePath() -> String {
        guard let filePath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first else { return "" }
        return filePath
    }
    
    func mimeType(pathExtension: String) -> String {
        
        let defaultMIMEType = "application/octet-stream"
        // 获取⽂件名后缀标记
        guard let tag = pathExtension.components(separatedBy: "/").last?
            .components(separatedBy: ".").last?
            .trimmingCharacters(in: .whitespacesAndNewlines) else { return defaultMIMEType }
        // 异常则返回⼆进制通⽤类型
        guard let uti = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, tag as CFString, nil)?.takeRetainedValue(),
              let mimeType = UTTypeCopyPreferredTagWithClass(uti, kUTTagClassMIMEType)?.takeRetainedValue()
        else { return defaultMIMEType }
        return mimeType as String
    }
}
