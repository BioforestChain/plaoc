//
//  RefreshManager.swift
//  BFS
//
//  Created by ui03 on 2022/8/26.
//

import UIKit

class RefreshManager: NSObject {

    private var task: URLSessionDataTask?
    
    var lastUpdateTime: Int? {
        return UserDefaults.standard.object(forKey: "updateTime") as? Int
    }
    
    static func fetchLastUpdateTime(fileName: String) -> Int? {
        return UserDefaults.standard.object(forKey: fileName) as? Int
    }
    
    static func saveLastUpdateTime(fileName: String, time: Int) {
        UserDefaults.standard.setValue(time, forKey: fileName)
    }
    
    func loadUpdateRequestInfo(fileName: String? = nil, urlString: String? = nil, isCompare: Bool = false) {
        
        guard urlString != nil else { return }
        guard let url = URL(string: urlString!) else { return }
        let request = URLRequest(url: url)
        let session = URLSession.shared
        let task = session.dataTask(with: request) { data, response, error in
            //缓存到bfs-app-id/tmp/autoUpdate/文件中 格式为TIME.json  需要校验
            guard data != nil else { return }
            guard let result = String(data: data!, encoding: .utf8) else { return }
            let dict = ChangeTools.stringValueDic(result)
            let dataDict = dict?["data"] as? [String:Any]
            if fileName != nil {
                RefreshManager.saveLastUpdateTime(fileName: fileName!, time: Date().timeStamp)
                batchManager.writeUpdateContent(fileName: fileName!, json: dataDict)
                if isCompare {
                    //TODO 发送比较版本信息
                    operateMonitor.refreshCompleteMonitor.onNext(fileName!)
                }
            }
        }
        
        task.resume()
        
    }
    
    func cancelUpdateRequest() {
        task?.cancel()
    }
    
    func testDict() -> [String:Any] {
        
        var dict: [String:Any] = [:]
        dict["version"] = "4.5"
        dict["releaseNotes"] = "test"
        dict["releaseName"] = "测试"
        dict["releaseDate"] = "2022.08.06"
        dict["files"] = [["url":"http://dldir1.qq.com/qqfile/qq/QQ7.9/16621/QQ7.9.exe",
                         "size": 102984,
                         "sha512": "haha"]]
        return dict
    }
}
