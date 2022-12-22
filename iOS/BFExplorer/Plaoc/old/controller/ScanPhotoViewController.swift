//
//  ScanPhotoViewController.swift
//  Plaoc-iOS
//
//  Created by mac on 2022/7/22.
//

import UIKit
import AVFoundation

class ScanPhotoViewController: UIViewController {

    private var scanRectView: UIView?
    private var device: AVCaptureDevice?
    private var input: AVCaptureDeviceInput?
    private var output: AVCaptureMetadataOutput?
    private var session: AVCaptureSession?
    private var preview: AVCaptureVideoPreviewLayer?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.view.backgroundColor = .black
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        startScan()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.session?.stopRunning()
        
    }
    
    //开始扫描
    func startScan() {
        
        do {
            self.device = AVCaptureDevice.default(for: .video)
            if self.device != nil {
                self.input = try? AVCaptureDeviceInput(device: self.device!)
            }
            
            self.output = AVCaptureMetadataOutput()
            self.output?.setMetadataObjectsDelegate(self, queue: .main)
            
            
            self.session = AVCaptureSession()
            guard self.input != nil, self.session!.canAddInput(self.input!) else { return }
            guard self.output != nil, self.session!.canAddOutput(output!) else { return }
            
            if UIScreen.main.bounds.height < 500 {
                self.session?.sessionPreset = AVCaptureSession.Preset.vga640x480
            } else {
                self.session?.sessionPreset = AVCaptureSession.Preset.high
            }
            
            session?.addInput(input!)
            session?.addOutput(output!)
            // 注意点: 设置数据类型一定要在输出对象添加到会话之后才能设置
            self.output?.metadataObjectTypes = [.qr, .ean13, .ean8,.code93, .code128, .code39, .code93, .code39Mod43]
            
            let scanSize = CGSize(width: UIScreen.main.bounds.size.width * 0.75, height: UIScreen.main.bounds.size.width * 0.75)
            var scanRect = CGRect(x: (UIScreen.main.bounds.size.width - scanSize
                .width) * 0.5, y: (UIScreen.main.bounds.size.height - scanSize
                    .width) * 0.5, width: scanSize.width, height: scanSize.height)
            
            //计算rectOfInterest 注意x,y交换位置
            scanRect = CGRect(x:scanRect.origin.y / UIScreen.main.bounds.size.height,
                              y:scanRect.origin.x / UIScreen.main.bounds.size.width,
                              width:scanRect.size.height / UIScreen.main.bounds.size.height,
                              height:scanRect.size.width / UIScreen.main.bounds.size.width)
            self.output?.rectOfInterest = scanRect
            
            self.preview = AVCaptureVideoPreviewLayer(session: self.session!)
            self.preview?.videoGravity = AVLayerVideoGravity.resizeAspectFill
            self.preview?.frame = UIScreen.main.bounds
            self.view.layer.insertSublayer(self.preview!, at: 0)
            
            self.scanRectView = UIView(frame: CGRect(x: 0, y: 0, width: scanSize.width, height: scanSize.height))
            self.scanRectView?.center = CGPoint(x: UIScreen.main.bounds.midX, y: UIScreen.main.bounds.midY)
            self.scanRectView?.layer.borderColor = UIColor.green.cgColor
            self.scanRectView?.layer.borderWidth = 1
            self.view.addSubview(self.scanRectView!)
            
            self.session?.startRunning()
            
            //通过代码拉近镜头焦距，放大内容区域让机器更好的识别
            do {
                try self.device?.lockForConfiguration()
            } catch _ {
                print("error: lockForConfiguration")
            }
            self.device?.videoZoomFactor = 1.5
            self.device?.unlockForConfiguration()
        } catch _ {
            //打印错误消息
            let alertController = UIAlertController(title: "温馨提醒",
                                                    message: "请在iPhone的\"设置-隐私-相机\"选项中,允许本程序访问您的相机",
                                                    preferredStyle: .alert)
            let cancelAction = UIAlertAction(title: "确定", style: .cancel, handler: nil)
            alertController.addAction(cancelAction)
            self.present(alertController, animated: true, completion: nil)
        }
    }
    
}

extension ScanPhotoViewController: AVCaptureMetadataOutputObjectsDelegate {
    
    func metadataOutput(_ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {
        if let metadataObj = metadataObjects.first as? AVMetadataMachineReadableCodeObject {
            let result = metadataObj.stringValue  //扫描结果
                
            if result != nil {
                
                self.session?.stopRunning()
            }
        }
        self.session?.stopRunning()
    }
}
