//
//  BatchScanManager.swift
//  Plaoc-iOS
//
//  Created by ui03 on 2022/10/18.
//

import UIKit

class BatchScanManager: BatchReadManager {

    private var scanImageDict: [String: String] = [:]
    override func filePath() -> String {
        guard let filePath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first else { return "" }
        return filePath + "/scan-app"
    }
    
    override func saveUpdateInfoFilePath() -> String {
        guard let filePath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first else { return "" }
        return filePath + "/scan-app"
    }
    
    func writeLinkJson(fileName: String, dict: [String:Any]) {
        scanImageDict[fileName] = dict["icon"] as? String
        let jsonString = ChangeTools.dicValueString(dict) ?? ""
        guard jsonString.count > 0 else { return }
        let path = filePath() + "/\(fileName)/boot/"
        if !FileManager.default.fileExists(atPath: path) {
            try? FileManager.default.createDirectory(atPath: path, withIntermediateDirectories: true)
        }
        let linkPath = path + "link.json"
        if !FileManager.default.fileExists(atPath: linkPath) {
            FileManager.default.createFile(atPath: linkPath, contents: nil)
        }
        do {
            try jsonString.write(toFile: linkPath, atomically: true, encoding: .utf8)
        } catch {
            print(error.localizedDescription)
        }
    }
}
