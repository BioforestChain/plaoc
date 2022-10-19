//
//  BatchFileManager.swift
//  BFS
//
//  Created by ui03 on 2022/9/6.
//

import UIKit
import RxSwift

enum FilePathType {
    case system
    case recommend
    case scan
    case none
}

let batchManager = BatchFileManager()

class BatchFileManager: NSObject {

    private let disposeBag = DisposeBag()
    private let sysManager = BatchSystemManager()
    private let recommendManager = BatchRecommendManager()
    private let scanManager = BatchScanManager()
    private var fileType: [String:FilePathType] = [:]
    private var appNames: [String:String] = [:]
    private var appImages: [String:UIImage?] = [:]
    private var fileLinkDict: [String:String] = [:]
    
    private(set) var appFilePaths: [String] = []
    
    func initBatchFile() {
        
        let recommendFiles = recommendManager.readAppSubFile()
        let systemFiles = sysManager.readAppSubFile()
        let scanFiles = scanManager.readAppSubFile()
        fetchFileTypes(recommendFiles: recommendFiles, systemFiles: systemFiles, scanFiles: scanFiles)
        fetchAppNames()
        fetchAppIcons()
        
        GlobalTimer.shared.StartTimer()
        
        operateMonitor.refreshCompleteMonitor.subscribe(onNext: { [weak self] fileName in
            guard let strongSelf = self else { return }
            strongSelf.downloadNewFile(fileName: fileName)
        }).disposed(by: disposeBag)
        
    }
    //根据文件名获取app名称
    func currentAppName(fileName: String) -> String {
        return appNames[fileName] ?? ""
    }
    //根据文件名获取app图片
    func currentAppImage(fileName: String) -> UIImage? {
        return appImages[fileName] ?? nil
    }
    //根据文件名获取app类型
    func currentAppType(fileName: String) -> FilePathType {
        return fileType[fileName] ?? .none
    }
    
    //点击recommend文件
    func clickRecommendAppAction(fileName: String) {
        //1、从bfs-app-id/tmp/autoUpdate/缓存中读取当下的新json数据,并请求更新
        guard fileName.count > 0 else { return }
        let currentURLString = refreshInfoFromCacheInfo(fileName: fileName)
        alertUpdateViewController(fileName: fileName, urlstring: currentURLString)
        fileLinkDict[fileName] = currentURLString
    }
    //写入轮询更新数据
    func writeUpdateContent(fileName: String, json: [String:Any]?) {
        guard json != nil else { return }
        let type = currentAppType(fileName: fileName)
        if type == .system {
            sysManager.writeUpdateInfoToTmpFile(fileName: fileName, json: json!)
        } else if type == .recommend {
            recommendManager.writeUpdateInfoToTmpFile(fileName: fileName, json: json!)
        } else if type == .scan {
            scanManager.writeUpdateInfoToTmpFile(fileName: fileName, json: json!)
        }
    }
    
    //更新文件状态为已下载
    func updateFileType(fileName: String) {
        if fileType[fileName] != nil {
            fileType[fileName] = .system
            appNames[fileName] = sysManager.appName(fileName: fileName)
            appImages[fileName] = sysManager.appIcon(fileName: fileName)
        }
    }
    //更新文件状态为扫码
    func updateScanType(fileName: String) {
        fileType[fileName] = .scan
        appNames[fileName] = scanManager.appName(fileName: fileName)
    }
    //获取扫码的图片地址
    func scanImageURL(fileName: String) -> String {
        return scanManager.appIconUrlString(fileName: fileName) ?? ""
    }
    //获取扫码后app的下载地址
    func scanDownloadURLString(fileName: String) -> String {
        return refreshInfoFromCacheInfo(fileName: fileName) ?? ""
    }

    //定时刷新
    func fetchRegularUpdateTime() {
        
        guard appFilePaths.count > 0 else { return }
        let updateArray = appFilePaths//.filter{ currentAppType(fileName: $0) == .recommend }
//        guard updateArray.count > 0 else { return }
        let refreshManager = RefreshManager()
        for filename in updateArray {
            if isNeedUpdate(fileName: filename) {
                let updateString = autoUpdateURLString(fileName: filename)
                refreshManager.loadUpdateRequestInfo(fileName: filename, urlString: updateString, isCompare: false)
            }
        }
    }
    //判断是否过了缓存时间
    func isNeedUpdate(fileName: String) -> Bool {
        
        guard let lastTime = RefreshManager.fetchLastUpdateTime(fileName: fileName) else { return false }
        guard let maxAge = autoUpdateMaxAge(fileName: fileName) else { return false }
        let currentDate = Date().timeStamp
        if currentDate - lastTime > maxAge {
            return true
        }
        return false
    }
    
    //获取system-app最新的版本信息
    func fetchSystemAppNewVersion(fileName: String, urlString: String) {
//        guard let urlString = sysManager.readAutoUpdateURLInfo(fileName: fileName) else { return }
//        let refreshManager = RefreshManager()
//        refreshManager.loadUpdateRequestInfo(fileName: fileName, urlString: urlString, isCompare: true)
        
        //system-app升级操作
        //1、点击升级，退回到桌面界面
        //2、开始动画，下载文件
        operateMonitor.startAnimationMonitor.onNext(fileName)
        BFSNetworkManager.shared.loadAutoUpdateInfo(fileName: fileName, urlString: urlString)
        
    }
    
    //system-app升级后，更新本地文件，提供给app使用
    func updateSystemAppFile(fileName: String, path: String) {
        guard let filePath = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first else { return }
        let desPath = filePath + "/system-app"
        
        NVHTarGzip.sharedInstance().unTarGzipFile(atPath: path, toPath: desPath) { error in
            if error == nil {
                let schemePath = desPath + "/\(fileName)/sys"   //后面看返回数据修改
                Schemehandler.setupHTMLCache(fileName: fileName, fromPath: schemePath)
                RefreshManager.saveLastUpdateTime(fileName: fileName, time: Date().timeStamp)
            }
        }
    }
    
    //扫码添加安装的app数据
    func addAPPFromScan(fileName: String, dict: [String:Any]) {
        scanManager.writeLinkJson(fileName: fileName, dict: dict)
    }
    
    //更新信息下载完后，重新下载项目文件,  可能不需要判断system-app 看最后system-app升级时的需求
    private func downloadNewFile(fileName: String) {
        
        let type = currentAppType(fileName: fileName)
        if type == .recommend {
            downloadRecommendFile(fileName: fileName)
        } else if type == .system {
            //暂时注释掉
           // downloadSystemFile(fileName: fileName)
        }
    }
    //更新信息下载完后，重新下载Recommend项目文件
    private func downloadRecommendFile(fileName: String) {
        //3、如果有最新信息，停止缓存中的更新
        guard hasNewUpdateInfo(fileName: fileName) else { return }
        //4再从从bfs-app-id/tmp/autoUpdate/缓存中读取最新的json数据
        guard let newURLString = refreshInfoFromCacheInfo(fileName: fileName) else { return }
        let currentURLString = fileLinkDict[fileName]
        if currentURLString == nil {
            operateMonitor.startAnimationMonitor.onNext(fileName)
            BFSNetworkManager.shared.loadAutoUpdateInfo(fileName: fileName, urlString: newURLString)
        } else {
            //5、重新下载
            reloadUpdateFile(fileName: fileName, cancelUrlString: currentURLString, urlString: newURLString)
        }
    }
    //更新信息下载完后，重新下载System项目文件
    private func downloadSystemFile(fileName: String) {
        //3、如果有最新信息，弹框
        guard isSystemUpdate(fileName: fileName) else { return }
        //4再从从bfs-app-id/tmp/autoUpdate/缓存中读取最新的json数据
        guard let newURLString = refreshInfoFromCacheInfo(fileName: fileName) else { return }
        alertUpdateViewController(fileName: fileName, urlstring: newURLString)
        
    }
    
    //从bfs-app-id/tmp/autoUpdate/缓存中读取url数据
    private func refreshInfoFromCacheInfo(fileName: String) -> String? {
        let cacheInfo = readCacheUpdateInfo(fileName: fileName)
        let caches = cacheInfo?["files"] as? [[String:Any]]
        let fileInfo = caches?.first
        guard let urlstring = fileInfo?["url"] as? String else { return nil }
        return urlstring
    }
    
    //读取缓存更新的信息
    private func readCacheUpdateInfo(fileName: String) -> [String:Any]? {
        
        let type = currentAppType(fileName: fileName)
        if type == .system {
            return sysManager.readCacheUpdateInfo(fileName: fileName)
        } else if type == .recommend {
            return recommendManager.readCacheUpdateInfo(fileName: fileName)
        } else if type == .scan {
            return scanManager.readCacheUpdateInfo(fileName: fileName)
        }
        return nil
    }
    
    //从link.json的autoUpdate读取更新信息
    private func refreshNewAutoUpdateInfo(fileName: String, isCompare: Bool) {
        guard let updateString = autoUpdateURLString(fileName: fileName) else { return }
        let refreshManager = RefreshManager()
        refreshManager.loadUpdateRequestInfo(fileName: fileName, urlString: updateString, isCompare: isCompare)
    }
    
    //取消缓存的下载信息，重新下载最新信息
    private func reloadUpdateFile(fileName: String, cancelUrlString: String?, urlString: String?) {
        BFSNetworkManager.shared.cancelNetworkRequest(urlString: cancelUrlString)
        if urlString != nil {
            operateMonitor.startAnimationMonitor.onNext(fileName)
            BFSNetworkManager.shared.loadAutoUpdateInfo(fileName: fileName, urlString: urlString!)
        }
    }
    
    //获取自动更新的url
    private func autoUpdateURLString(fileName: String) -> String? {
        let type = currentAppType(fileName: fileName)
        if type == .system {
            return sysManager.readAutoUpdateURLInfo(fileName: fileName)
        } else if type == .recommend {
            return recommendManager.readAutoUpdateURLInfo(fileName: fileName)
        } else if type == .scan {
            return scanManager.readAutoUpdateURLInfo(fileName: fileName)
        }
        return nil
    }
    
    //获取自动更新的时间间隔
    private func autoUpdateMaxAge(fileName: String) -> Int? {
        let type = currentAppType(fileName: fileName)
        if type == .system {
            return sysManager.readAutoUpdateMaxAge(fileName: fileName)
        } else if type == .recommend {
            return recommendManager.readAutoUpdateMaxAge(fileName: fileName)
        } else if type == .scan {
            return scanManager.readAutoUpdateMaxAge(fileName: fileName)
        }
        return nil
    }
    
    //判断是否有新的更新消息
    private func hasNewUpdateInfo(fileName: String) -> Bool {
        let type = currentAppType(fileName: fileName)
        if type == .system {
            return sysManager.isNewUpdateInfo(fileName: fileName)
        } else if type == .recommend {
            return recommendManager.isNewUpdateInfo(fileName: fileName)
        } else if type == .scan {
            return scanManager.isNewUpdateInfo(fileName: fileName)
        }
        return false
    }
    
    //判断system-app是否有新的更新消息
    private func isSystemUpdate(fileName: String) -> Bool {
        guard let currentVersion = sysManager.readMetadataVersion(fileName: fileName) else { return false }
        let cacheDict = sysManager.readCacheUpdateInfo(fileName: fileName)
        guard let version = cacheDict?["version"] as? String  else { return false }
        let result = version.versionCompare(oldVersion: currentVersion)
        if result == .orderedAscending {
            return true
        }
        return false
    }
    
    //获取文件夹的类型
    private func fetchFileTypes(recommendFiles: [String], systemFiles: [String], scanFiles: [String]) {
        
        guard recommendFiles.count > 0 || systemFiles.count > 0 || scanFiles.count > 0 else { return }
//        if recommendFiles.count == 0 {
//            for fileName in systemFiles {
//                fileType[fileName] = .system
//            }
//            appFilePaths = systemFiles
//            return
//        }
//
//        if systemFiles.count == 0 {
//            for fileName in recommendFiles {
//                fileType[fileName] = .recommend
//            }
//            appFilePaths = recommendFiles
//            return
//        }
        
        let array = recommendFiles + systemFiles + scanFiles
        let setList = Set(array)
        appFilePaths = Array(setList)
        
        for fileName in setList {
            if systemFiles.contains(fileName) {
                fileType[fileName] = .system
            } else if scanFiles.contains(fileName) {
                fileType[fileName] = .scan
            } else {
                fileType[fileName] = .recommend
            }
        }
    }
    //获取所有的icon名称
    private func fetchAppNames() {
        for (key,type) in fileType {
            if type == .system {
                appNames[key] = sysManager.appName(fileName: key)
            } else if type == .recommend {
                appNames[key] = recommendManager.appName(fileName: key)
            } else if type == .scan {
                appNames[key] = scanManager.appName(fileName: key)
            }
        }
    }
    //获取所有的icon图片
    private func fetchAppIcons() {
        for (key,type) in fileType {
            if type == .system {
                appImages[key] = sysManager.appIcon(fileName: key)
            } else if type == .recommend {
                appImages[key] = recommendManager.appIcon(fileName: key)
            }
        }
    }
    
    //下载弹框
    private func alertUpdateViewController(fileName: String, urlstring: String?) {
        let alertVC = UIAlertController(title: "确认下载更新吗？", message: nil, preferredStyle: .alert)
        let sureAction = UIAlertAction(title: "确认", style: .default) { action in
            if urlstring != nil {
                operateMonitor.startAnimationMonitor.onNext(fileName)
                BFSNetworkManager.shared.loadAutoUpdateInfo(fileName: fileName, urlString: urlstring!)
            }
            let type = self.currentAppType(fileName: fileName)
//            if appType != nil {
//                type = appType!
//            }
            if type == .system {
                operateMonitor.backMonitor.onNext(fileName)
            } else if type == .recommend {
                self.refreshNewAutoUpdateInfo(fileName: fileName, isCompare: true)
            } else if type == .scan {
                self.refreshNewAutoUpdateInfo(fileName: fileName, isCompare: true)
            }
        }
        let cancelAction = UIAlertAction(title: "取消", style: .cancel) { action in
            let type = self.currentAppType(fileName: fileName)
            if type == .recommend {
                self.refreshNewAutoUpdateInfo(fileName: fileName, isCompare: false)
            }
        }
        alertVC.addAction(sureAction)
        alertVC.addAction(cancelAction)
        
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        let controller = appDelegate.window?.rootViewController
        controller?.present(alertVC, animated: true)
    }
}
