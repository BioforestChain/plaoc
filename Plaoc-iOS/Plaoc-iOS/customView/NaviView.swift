//
//  NaviView.swift
//  DWebBrowser
//
//  Created by mac on 2022/6/16.
//

import UIKit

class NaviView: UIView {

    var titleString: String? {
        didSet {
            titleLabel.text = titleString
        }
    }
    
    var tineColor: UIColor? {
        didSet {
            titleLabel.textColor = tineColor
            backButton.setTitleColor(tineColor, for: .normal)
        }
    }
    
    var hiddenBtn: Bool = false {
        didSet {
            addButton.isHidden = hiddenBtn
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.addSubview(backButton)
        self.addSubview(titleLabel)
        self.addSubview(addButton)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    lazy var backButton: UIButton = {
        let button = UIButton(type: .system)
        button.frame = CGRect(x: 16, y: 0, width: 50, height: self.frame.height)
        button.setTitle("返回", for: .normal)
        button.contentHorizontalAlignment = .left
        button.titleLabel?.font = UIFont.systemFont(ofSize: 16)
        button.addTarget(self, action: #selector(backAction), for: .touchUpInside)
        return button
    }()
    
    lazy var titleLabel: UILabel = {
        let label = UILabel(frame: CGRect(x: (self.frame.width - 200) * 0.5, y: 0, width: 200, height: self.frame.height))
        label.font = UIFont.systemFont(ofSize: 18)
        label.textAlignment = .center
        return label
    }()
    
    lazy var addButton: UIButton = {
        let button = UIButton(type: .contactAdd)
        button.menu = menuAction()
        button.showsMenuAsPrimaryAction = true
        button.frame = CGRect(x: self.frame.width - 60, y: 0, width: 44, height: 44)
        button.isHidden = true
        return button
    }()
}

extension NaviView {
    
    @objc private func backAction() {
        let controller = currentViewController()
        controller.navigationController?.popViewController(animated: true)
    }
    
    private func menuAction() -> UIMenu {
        
        let destruct = UIAction(title: "Destruct", attributes: .destructive) { _ in }

        let items = UIMenu(title: "More", options: .displayInline, children: [
            UIAction(title: "mic", image: UIImage(systemName: "mic"), handler: { _ in }),
            UIAction(title: "camera", image: UIImage(systemName: "camera"), handler: { _ in }),
            UIAction(title: "video", image: UIImage(systemName: "video"), handler: { _ in })
        ])
        let subItems = [
            UIAction(title: "画像1", image: UIImage(named: "alt"), handler: { _ in }),
            UIAction(title: "画像2", image: UIImage(named: "alt"), handler: { _ in }),
            UIAction(title: "画像3", image: UIImage(named: "alt"), handler: { _ in }),        ]
        //ボタンタップですぐ表示設定
        let photo = UIMenu(title: "photo", children: subItems)
//        sender.showsMenuAsPrimaryAction = true
        //itemの追加
        return UIMenu(title: "菜单", children: [items,photo,destruct])
    
    }
}
