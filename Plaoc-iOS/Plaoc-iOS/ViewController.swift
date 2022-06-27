//
//  ViewController.swift
//  Plaoc-iOS
//
//  Created by mac on 2022/6/14.
//

import UIKit

let screen_width = UIScreen.main.bounds.width
let screen_height = UIScreen.main.bounds.height

class ViewController: UIViewController, UIGestureRecognizerDelegate {

    private let dataSource: [String] = ["example","statusbar","http://127.0.0.1:5500/Plaoc-iOS/Plaoc-iOS/jsFile/test/index.html"]
    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor = .white
        self.title = "样例"
        self.view.addSubview(tableView)
        
        self.navigationController?.interactivePopGestureRecognizer?.delegate = self
    }

    lazy private var tableView: UITableView = {
        let tableView = UITableView(frame: CGRect(x: 0, y: UIDevice.current.statusBarHeight() + 44, width: screen_width, height: screen_height - UIDevice.current.statusBarHeight() - 44), style: .plain)
        tableView.dataSource = self
        tableView.delegate = self
        return tableView
    }()
    
    func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        
        if gestureRecognizer == self.navigationController?.interactivePopGestureRecognizer {
            return self.navigationController!.viewControllers.count > 1
        }
        return true
    }
}

extension ViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dataSource.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell = tableView.dequeueReusableCell(withIdentifier: "cell")
        if cell == nil {
            cell = UITableViewCell(style: .default, reuseIdentifier: "cell")
        }
        
        if indexPath.row < dataSource.count {
            cell?.textLabel?.text = dataSource[indexPath.row]
        }
        return cell!
    }
}

extension ViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        let controller = WebViewViewController()
        if let path = Bundle.main.path(forResource: dataSource[indexPath.row], ofType: "html") {
            controller.urlString = "iosqmkkx:/index.html"//path
        } else {
            controller.urlString = dataSource[indexPath.row];
        }
        self.navigationController?.pushViewController(controller, animated: true)
    }
}
