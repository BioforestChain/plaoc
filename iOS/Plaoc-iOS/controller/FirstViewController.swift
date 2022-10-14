//
//  FirstViewController.swift
//  Plaoc-iOS
//
//  Created by ui03 on 2022/10/13.
//

import UIKit
import RxSwift

class FirstViewController: UIViewController {

    private var appNames: [String] = []
    private var buttons: [UIButton] = []
    private var labels: [UILabel] = []
    private let disposeBag = DisposeBag()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.view.backgroundColor = .white
        
        batchManager.initBatchFile()
        
        appNames = batchManager.appFilePaths
        
        for i in stride(from: 0, to: appNames.count, by: 1) {
            let name = appNames[i]
            let button = UIButton(frame: CGRect(x: 100 + i * 90, y: 200, width: 60, height: 60))
            button.addTarget(self, action: #selector(tap(sender:)), for: .touchUpInside)
            button.setImage(batchManager.currentAppImage(fileName: name), for: .normal)
            button.tag = i
            button.layer.cornerRadius = 10
            button.layer.masksToBounds = true
            self.view.addSubview(button)
            buttons.append(button)
            
            let label = UILabel(frame: CGRect(x: button.frame.minX, y: 280, width: 60, height: 20))
            label.textAlignment = .center
            label.textColor = .black
            label.text = batchManager.currentAppName(fileName: name)
            self.view.addSubview(label)
            labels.append(label)
        }
        
        NotificationCenter.default.addObserver(self, selector: #selector(update(noti:)), name: NSNotification.Name.progressNotification, object: nil)
        
        operateMonitor.startAnimationMonitor.subscribe(onNext: { [weak self] fileName in
            guard let strongSelf = self else { return }
            if let index = strongSelf.appNames.firstIndex(of: fileName) {
                let button = strongSelf.buttons[index]
                button.setupForAppleReveal()
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
                    batchManager.updateFileType(fileName: fileName!)
                    if let index = self.appNames.firstIndex(of: fileName!) {
                        let button = self.buttons[index]
                        button.setImage(batchManager.currentAppImage(fileName: fileName!), for: .normal)
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

        guard sender.tag < appNames.count else { return }
        let name = appNames[sender.tag]
        let type = batchManager.currentAppType(fileName: name)
        if type == .system {
            let second = WebViewViewController()
            second.fileName = name
            second.urlString = "iosqmkkx:/index.html"
            self.navigationController?.pushViewController(second, animated: true)
        } else if type == .recommend {
            batchManager.clickRecommendAppAction(fileName: name)
        }
    }

}
