//
//  FirstViewController.swift
//  Plaoc-iOS
//
//  Created by ui03 on 2022/10/13.
//

import UIKit
import RxSwift
import SDWebImage

class FirstViewController: UIViewController {

    private var appNames: [String] = []
    private var buttons: [UIButton] = []
    private var labels: [UILabel] = []
    private let disposeBag = DisposeBag()
    private var jsCore: JSCoreManager!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.view.backgroundColor = .white
        
        appNames = BatchFileManager.shared.appFilePaths
        
        for i in stride(from: 0, to: appNames.count + 1, by: 1) {
            if i == appNames.count {
                let button = UIButton(frame: CGRect(x: 30 + i * 90, y: 200, width: 60, height: 60))
                button.addTarget(self, action: #selector(tap(sender:)), for: .touchUpInside)
                button.setTitle("测试", for: .normal)
                button.setTitleColor(.black, for: .normal)
                button.tag = i
                self.view.addSubview(button)
                buttons.append(button)
            } else {
                let name = appNames[i]
                let button = UIButton(frame: CGRect(x: 30 + i * 90, y: 200, width: 60, height: 60))
                button.addTarget(self, action: #selector(tap(sender:)), for: .touchUpInside)
                let type = BatchFileManager.shared.currentAppType(fileName: name)
                if type == .scan {
                    let urlString = BatchFileManager.shared.scanImageURL(fileName: name)
                    button.sd_setImage(with: URL(string: urlString), for: .normal)
                } else {
                    button.setImage(BatchFileManager.shared.currentAppImage(fileName: name), for: .normal)
                }
                button.tag = i
                button.layer.cornerRadius = 10
                button.layer.masksToBounds = true
                self.view.addSubview(button)
                buttons.append(button)
                
                let label = UILabel(frame: CGRect(x: button.frame.minX, y: 280, width: 60, height: 20))
                label.textAlignment = .center
                label.textColor = .black
                label.text = BatchFileManager.shared.currentAppName(fileName: name)
                self.view.addSubview(label)
                labels.append(label)
            }
        }
        
        NotificationCenter.default.addObserver(self, selector: #selector(update(noti:)), name: NSNotification.Name.progressNotification, object: nil)
        
        operateMonitor.startAnimationMonitor.subscribe(onNext: { [weak self] fileName in
            guard let strongSelf = self else { return }
            if let index = strongSelf.appNames.firstIndex(of: fileName) {
                let button = strongSelf.buttons[index]
                DispatchQueue.main.async {
                    button.setupForAppleReveal()
                }
            }
        }).disposed(by: disposeBag)
    }
    
    @objc func update(noti: Notification) {
        print("download")
        guard let infoDict = noti.userInfo else { return }
        guard let type = infoDict["progress"] as? String else { return }
        let fileName = infoDict["fileName"] as? String
        let realName = infoDict["realName"] as? String
        //判断 如果包含 是既有的，如果没有新增
        if self.appNames.contains(fileName!) {
            
        } else {
            //新增iconApp
            self.appNames.append(fileName!)
            DispatchQueue.main.async {
                self.addScanAppUI()
                let button = self.view.viewWithTag(3) as! UIButton
                button.setupForAppleReveal()
            }
        }
        DispatchQueue.main.async {
            if type == "complete" {
                if fileName != nil {
                    var name = fileName
                    if fileName != realName {
                        name = realName
                        if self.appNames.contains(realName!) {
                            //1、删除刚下载的icon APP
                            if BatchFileManager.shared.isReplaceSameFile(fileName: realName!) {
                                //2、在旧的上面添加红点
                            }
                        }
                    }
                    BatchFileManager.shared.updateFileType(fileName: name!)
                    if let index = self.appNames.firstIndex(of: name!) {
                        let button = self.buttons[index]
                        button.setImage(BatchFileManager.shared.currentAppImage(fileName: fileName!), for: .normal)
                        button.startExpandAnimation()
                    }
                }
            }
        }
        
        if Double(type) != nil {
            
            var count = Double(type)!
            if count >= 0.98 {
                count = 0.98
            }
            if let index = self.appNames.firstIndex(of: fileName!) {
                let button = self.buttons[index]
                button.startProgressAnimation(progress: 1.0 - count)
            }
        }
        
    }
    var manager: BrowserManager!
    @objc func tap(sender: UIButton) {
      
        
//        if sender.tag == 2 {
//            let second = WebViewViewController()
//            second.fileName = "wallet"
//            second.urlString = "https://objectjson.waterbang.top"  //"https://wallet.plaoc.com/"
//            self.navigationController?.pushViewController(second, animated: true)
//            return
//        }
        let name = appNames[sender.tag]
        let type = BatchFileManager.shared.currentAppType(fileName: name)
        if type == .system {
            let second = WebViewViewController()
            second.fileName = name
            second.urlString = BatchFileManager.shared.systemWebAPPURLString(fileName: name)! //"iosqmkkx:/index.html"
            let type = BatchFileManager.shared.systemAPPType(fileName: name)
            let url = BatchFileManager.shared.systemWebAPPURLString(fileName: name) ?? ""
            if type == "web" {
                second.urlString = url
            } else {
                second.urlString = "iosqmkkx:/index.html"
            }
            self.navigationController?.pushViewController(second, animated: true)
        } else if type == .recommend {
            BatchFileManager.shared.clickRecommendAppAction(fileName: name)
        } else if type == .scan {
            BatchFileManager.shared.clickRecommendAppAction(fileName: name)
        }
    }
    
    func addScanAppUI() {
        let button = UIButton(frame: CGRect(x: 30 + 3 * 90, y: 200, width: 60, height: 60))
//                button.addTarget(self, action: #selector(tap(sender:)), for: .touchUpInside)
        button.setTitle("", for: .normal)
        button.backgroundColor = .red
        button.setTitleColor(.black, for: .normal)
        button.tag = 3
        button.layer.cornerRadius = 10
        button.layer.masksToBounds = true
        view.addSubview(button)
        buttons.append(button)
    }
    
    func addScanAppAction(name: String) {
        let scanURLString = BatchFileManager.shared.scanDownloadURLString(fileName: name)
        guard scanURLString.count > 0 else { return }
        BFSNetworkManager.shared.loadAutoUpdateInfo(fileName: name, urlString: scanURLString)
        let button = self.view.viewWithTag(3) as? UIButton
        button!.setupForAppleReveal()
    }

}


