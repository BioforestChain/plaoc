package org.bfchain.libappmgr.utils

import android.app.Application

/** 通过反射获取当前应用的Application、Context */
class AppContextUtil {

    companion object {
        val sInstance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PlaocApplication()
        }

        private fun PlaocApplication(): Application? {
            try {
                var acThreadClass =
                    Class.forName("android.app.ActivityThread") ?: return null

                var acThreadMethod =
                    acThreadClass.getMethod("currentActivityThread") ?: return null

                acThreadMethod.isAccessible = true

                var activityThread = acThreadMethod.invoke(null)

                var appMethod =
                    activityThread.javaClass.getMethod("getApplication") ?: return null

                return appMethod.invoke(activityThread) as Application
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}