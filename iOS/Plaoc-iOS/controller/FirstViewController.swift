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
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.view.backgroundColor = .white
        
//        batchManager.initBatchFile()
        
        appNames = BatchFileManager.shared.appFilePaths
        
        for i in stride(from: 0, to: appNames.count + 1, by: 1) {
            if i == appNames.count {
                let button = UIButton(frame: CGRect(x: 30 + i * 90, y: 200, width: 60, height: 60))
                button.addTarget(self, action: #selector(tap(sender:)), for: .touchUpInside)
                button.setTitle("测试", for: .normal)
                button.setTitleColor(.black, for: .normal)
                button.tag = i
                self.view.addSubview(button)
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
        guard let infoDict = noti.userInfo else { return }
        guard let type = infoDict["progress"] as? String else { return }
        let fileName = infoDict["fileName"] as? String
        DispatchQueue.main.async {
            if type == "complete" {
                if fileName != nil {
                    BatchFileManager.shared.updateFileType(fileName: fileName!)
                    if let index = self.appNames.firstIndex(of: fileName!) {
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
    
    @objc func tap(sender: UIButton) {

        let name = appNames[sender.tag]
        let type = BatchFileManager.shared.currentAppType(fileName: name)
        if type == .system {
            let second = WebViewViewController()
            second.fileName = name
            second.urlString = "iosqmkkx:/index.html"
            self.navigationController?.pushViewController(second, animated: true)
        } else if type == .recommend {
            BatchFileManager.shared.clickRecommendAppAction(fileName: name)
        } else if type == .scan {
            BatchFileManager.shared.clickRecommendAppAction(fileName: name)
        }
    }
    
    func addScanAppUI(name: String) {
        let button = UIButton(frame: CGRect(x: 30 + 3 * 90, y: 200, width: 60, height: 60))
//                button.addTarget(self, action: #selector(tap(sender:)), for: .touchUpInside)
        button.setTitle(name, for: .normal)
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
