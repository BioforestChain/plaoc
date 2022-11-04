//
//  ThirdViewController.swift
//  Plaoc-iOS
//
//  Created by mac on 2022/7/19.
//

import UIKit
import RxSwift

typealias ThirdCallback = (String) -> Void

class ThirdViewController: UIViewController {

    var callback: ThirdCallback?
    private let disposeBag = DisposeBag()
    override func viewDidLoad() {
        super.viewDidLoad()

        self.view.backgroundColor = .white
        
        let button = UIButton(frame: CGRect(x: 90, y: 200, width: 60, height: 60))
        button.addTarget(self, action: #selector(tap(sender:)), for: .touchUpInside)
        button.setTitle("测试", for: .normal)
        button.setTitleColor(.black, for: .normal)
        self.view.addSubview(button)
        
        operateMonitor.scanMonitor.subscribe(onNext: { [weak self] (fileName,dict) in
            guard let strongSelf = self else { return }
            DispatchQueue.main.async {
                strongSelf.alertUpdateViewController(fileName: fileName, dataDict: dict)
                
            }
        }).disposed(by: disposeBag)
    }
    
    @objc func tap(sender: UIButton) {
        
        let refreshManager = RefreshManager()
        refreshManager.loadUpdateRequestInfo(fileName: nil, urlString: "https://shop.plaoc.com/KEJPMHLA/appversion.json", isCompare: false)
        
    }
    
    private func alertUpdateViewController(fileName: String, dataDict: [String:Any]) {
        let alertVC = UIAlertController(title: "确认下载更新吗？", message: nil, preferredStyle: .alert)
        let sureAction = UIAlertAction(title: "确认", style: .default) { action in
            
            batchManager.addAPPFromScan(fileName: fileName, dict: dataDict)
            batchManager.updateScanType(fileName: fileName)
            RefreshManager.saveLastUpdateTime(fileName: fileName, time: Date().timeStamp)
            batchManager.writeUpdateContent(fileName: fileName, json: dataDict)
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                self.navigationController?.popViewController(animated: true)
                self.callback?(fileName)
            }
        }
        let cancelAction = UIAlertAction(title: "取消", style: .cancel) { action in
            self.navigationController?.popViewController(animated: true)
        }
        alertVC.addAction(sureAction)
        alertVC.addAction(cancelAction)
        self.present(alertVC, animated: true)
    }
    
}
