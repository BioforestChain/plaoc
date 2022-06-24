//
//  BottomView.swift
//  DWebBrowser
//
//  Created by mac on 2022/6/16.
//

import UIKit

class BottomView: UIView {

    var hiddenBtn: Bool = false {
        didSet {
            addButton.isHidden = hiddenBtn
        }
    }
    
    var buttons: [BottomBarModel]? {
        didSet {
            guard buttons != nil else { return }
            let space: CGFloat = 16
            let width: CGFloat = 44
            for i in stride(from: 0, to: buttons!.count, by: 1) {
                let model = buttons![i]
                let button = UIButton(type: .contactAdd)
                let imageName = model.iconModel?.source ?? ""
                if model.iconModel?.type == "AssetIcon" {
                    button.sd_setImage(with: URL(string: imageName), for: .normal)
                } else {
                    if imageName.hasSuffix("svg") {
                        button.setImage(UIImage.svgImageNamed(imageName, size: CGSize(width: width, height: width)), for: .normal)
                    } else {
                        button.setImage(UIImage(named: imageName), for: .normal)
                    }
                }
                button.isEnabled = model.disabled ?? true
                button.menu = menuAction()
                button.showsMenuAsPrimaryAction = true
                button.frame = CGRect(x: self.frame.width - CGFloat((i + 1)) * (width + space), y: 0, width: 44, height: 44)
                self.addSubview(button)
            }
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.backgroundColor = .white
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    lazy var addButton: UIButton = {
        let button = UIButton(type: .contactAdd)
        button.menu = menuAction()
        button.showsMenuAsPrimaryAction = true
        button.frame = CGRect(x: self.frame.width - 60, y: 0, width: 44, height: 44)
        button.isHidden = true
        return button
    }()

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
