

import UIKit
import Alamofire
import SSZipArchive

class BFSNetworkManager: NSObject {
    
    static let shared = BFSNetworkManager()
    
    private var requestDict: [String: DownloadRequest] = [:]

    func loadAutoUpdateInfo(fileName: String? = nil, urlString: String) {
        var name = fileName
        let timeStamp = "\(Date().milliStamp)"
        
        let request = AF.download(urlString).downloadProgress { progress in
            print(progress.fractionCompleted)  //进度值
            NotificationCenter.default.post(name: NSNotification.Name.progressNotification, object: nil, userInfo: ["progress": "\(progress.fractionCompleted)", "fileName": name ?? timeStamp])
        }.responseURL { response in
            print(response)
            switch response.result {
            case .success:
                //下载后的文件路径
                if response.fileURL != nil {
                    DispatchQueue.global().async {
                      //  let path = Bundle.main.bundlePath + "/recommend-app/www.bfsa"
                        
                        let filePath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first
                        
                        if filePath != nil {
                            var desPath = filePath! + "/system-app"
                            if name == nil {
                                desPath = NSTemporaryDirectory()
                            }
                            DispatchQueue.main.async {
                                NVHTarGzip.sharedInstance().unTarGzipFile(atPath: response.fileURL!.path, toPath: desPath) { error in
                                    if error == nil {
                                        var schemePath = ""
                                        var isReplace: Bool = false
                                        if name == nil {
                                            name = self.subFilePathNames(atPath: desPath).first
                                            if name != nil {
                                                schemePath = desPath + "\(name!)/sys"
                                                isReplace = self.versionCompare(name: name!)
                                                if isReplace {
                                                    self.copyItemToSystem(name: name!)
                                                }
                                            }
                                        } else {
                                            schemePath = desPath + "/\(name!)/sys"
                                            isReplace = true
                                        }
                                        if name != nil {
                                            if isReplace {
                                                Schemehandler.setupHTMLCache(fileName: name!, fromPath: schemePath)
                                                BatchFileManager.shared.updateRedHot(fileName: name!, statue: true)
                                            }
                                            RefreshManager.saveLastUpdateTime(fileName: name!, time: Date().timeStamp)
                                        }
                                        NotificationCenter.default.post(name: NSNotification.Name.progressNotification, object: nil, userInfo: ["progress": "complete", "fileName": fileName ?? timeStamp, "realName": name ?? ""])
                                    } else {
                                        NotificationCenter.default.post(name: NSNotification.Name.progressNotification, object: nil, userInfo: ["progress": "fail", "fileName": fileName ?? timeStamp])
                                    }
                                }
                            }
                        }
                    }
                }
            case .failure:
                NotificationCenter.default.post(name: NSNotification.Name.progressNotification, object: nil, userInfo: ["progress": "fail", "fileName": fileName ?? timeStamp])
            }
        }
        if name != nil {
            self.requestDict[name!] = request
        }
    }
    
    func cancelNetworkRequest(urlString: String?) {
        guard urlString != nil else { return }
        AF.session.getTasksWithCompletionHandler { dataTask, uploadTask, downloadTask in
            
            dataTask.forEach { task in
                if task.originalRequest?.url?.absoluteString == urlString {
                    //到时候需要添加取消的id
                    NotificationCenter.default.post(name: NSNotification.Name.progressNotification, object: nil, userInfo: ["progress": "cancel"])
                    task.cancel()
                }
            }
            uploadTask.forEach { task in
                if task.originalRequest?.url?.absoluteString == urlString {
                    NotificationCenter.default.post(name: NSNotification.Name.progressNotification, object: nil, userInfo: ["progress": "cancel"])
                    task.cancel()
                }
            }
            downloadTask.forEach { task in
                if task.originalRequest?.url?.absoluteString == urlString {
                    NotificationCenter.default.post(name: NSNotification.Name.progressNotification, object: nil, userInfo: ["progress": "cancel"])
                    task.cancel()
                }
            }
        }
    }
    
    func suspendRequest(fileName: String) {
        if let request = requestDict[fileName] {
            request.suspend()
        }
        
    }
    
    func resumeRequest(fileName: String) {
        if let request = requestDict[fileName] {
            request.resume()
        }
    }
    
    //读取文件夹下面的第一级文件夹
    private func subFilePathNames(atPath path: String) -> [String] {
        var fileList: [String] = []
        guard let filePaths = FileManager.default.subpaths(atPath: path) else { return fileList }
        for fileName in filePaths {
            var isDir: ObjCBool = true
            let fullPath = "\(path)/\(fileName)"
            if FileManager.default.fileExists(atPath: fullPath, isDirectory: &isDir) {
                if !isDir.boolValue {
                    
                } else {
                    //后续是不需要判断.的，因为这是临时添加的，后续从网络获取
                    if !fileName.contains("/"), !fileName.contains(".") {
                        fileList.append(fileName)
                    }
                }
            }
        }
        return fileList
    }
    
    private func versionCompare(name: String) -> Bool {
        
        guard let filePath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first else { return false }
        let desPath = filePath + "/system-app/\(name)"
        if FileManager.default.fileExists(atPath: desPath) {
            let tmpManager = BatchTempManager()
            let tmpVersion = tmpManager.tempAppVersion(name: name)
            let oldVersion = BatchFileManager.shared.systemAPPVersion(fileName: name)
            let result = tmpVersion.versionCompare(oldVersion: oldVersion)
            if result == .orderedAscending {
                return true
            }
            return false
        } else {
            return true
        }
    }
    
    private func copyItemToSystem(name: String) {
        guard let filePath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first else { return }
        let desPath = filePath + "/system-app/\(name)"
        let tmpPath = NSTemporaryDirectory() + name
        do {
            if !FileManager.default.fileExists(atPath: desPath) {
                try FileSystemManager.mkdir(at: URL(fileURLWithPath: filePath + "/system-app/"), recursive: true)
            }
            try FileManager.default.copyItem(atPath: tmpPath, toPath: desPath)
        } catch {
            print(error.localizedDescription)
        }
    }
}
