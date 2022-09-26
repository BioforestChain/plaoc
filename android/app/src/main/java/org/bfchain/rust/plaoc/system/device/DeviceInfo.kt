package org.bfchain.rust.plaoc.system.device

import org.bfchain.rust.plaoc.system.device.model.AppInfo
import org.bfchain.rust.plaoc.system.device.model.BatteryInfo

class DeviceInfo {

  fun getAppInfo(): String {
    return AppInfo().getAppInfo()
  }

  fun getBatteryInfo(): String {
    return BatteryInfo().getBatteryInfo()
  }
}
