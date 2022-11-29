//
//  SystemManager.swift
//  Plaoc-iOS
//
//  Created by ui03 on 2022/11/17.
//

import UIKit

class SystemManager: NSObject {

    
    func filePath() -> String {
        return Bundle.main.bundlePath + "/sys"
    }
    
    func fetchEntryPath() -> String? {
        guard let dict = readBFSAMatedataContent() else { return nil }
        guard let maniDict = dict["manifest"] as? [String:Any] else { return nil }
        guard var entryPath = maniDict["bfsaEntry"] as? String else { return nil }
        entryPath = entryPath.regexReplacePattern(pattern: "^(./|/|../)")
        let path = filePath() + "/HE74YAAL/" + entryPath
        return path
    }
    
    //读取matedata.json文件
    func readBFSAMatedataContent() -> [String:Any]? {
        let path = filePath() + "/HE74YAAL/boot/bfsa-metadata.json"
        let manager = FileManager.default
        guard let data = manager.contents(atPath: path) else { return nil }
        guard let content = String(data: data, encoding: .utf8) else { return nil }
        let mateDict = ChangeTools.stringValueDic(content)
        return mateDict
    }
}
