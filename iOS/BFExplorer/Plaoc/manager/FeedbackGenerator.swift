//
//  FeedbackGenerator.swift
//  Plaoc-iOS
//
//  Created by ui03 on 2022/12/16.
//

import UIKit
import CoreHaptics
import AudioToolbox

class FeedbackGenerator: NSObject {

    
    static func notificationFeedbackGenerator(style: UINotificationFeedbackGenerator.FeedbackType) {
        let generator = UINotificationFeedbackGenerator()
        generator.notificationOccurred(style)
    }
    
    static func impactFeedbackGenerator(style: UIImpactFeedbackGenerator.FeedbackStyle) {
        let gene = UIImpactFeedbackGenerator(style: style)
        gene.impactOccurred()
    }
    
    static func selectionFeedbackGenerator() {
        let gene = UISelectionFeedbackGenerator()
        gene.selectionChanged()
    }
    
    static func vibrate(_ duration: Double) {
        if CHHapticEngine.capabilitiesForHardware().supportsHaptics {
            do {
                let engine = try CHHapticEngine()
                try engine.start()
                engine.resetHandler = {
                    do {
                        try engine.start()
                    } catch {
                        AudioServicesPlayAlertSound(SystemSoundID(kSystemSoundID_Vibrate))
                    }
                }
                let intensity = CHHapticEventParameter(parameterID: .hapticIntensity, value: 1.0)
                let sharpness = CHHapticEventParameter(parameterID: .hapticSharpness, value: 1.0)
                
                let continuousEvent = CHHapticEvent(eventType: .hapticContinuous, parameters: [intensity,sharpness], relativeTime: 0.0, duration: duration)
                let pattern = try CHHapticPattern(events: [continuousEvent], parameters: [])
                let player = try engine.makePlayer(with: pattern)
                try player.start(atTime: 0)
            } catch {
                AudioServicesPlayAlertSound(SystemSoundID(kSystemSoundID_Vibrate))
            }
        } else {
            AudioServicesPlayAlertSound(SystemSoundID(kSystemSoundID_Vibrate))
        }
    }
}

extension PlaocHandleModel {
    // 触碰轻质量物体
    func hapticsImpactLight(param: String) -> Void {
        FeedbackGenerator.impactFeedbackGenerator(style: UIImpactFeedbackGenerator.FeedbackStyle.light)
    }
    
    // 警告分隔的振动通知
    func hapticsNotificationWarning(param: String) -> Void {
        FeedbackGenerator.notificationFeedbackGenerator(style: UINotificationFeedbackGenerator.FeedbackType.warning)
    }
    
    // 反馈振动
    func hapticsVibrate(param: String) -> Void {
        // 默认单击
        FeedbackGenerator.vibrate(Double(param) ?? 1)
    }
}
