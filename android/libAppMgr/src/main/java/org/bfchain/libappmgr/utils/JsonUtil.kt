package org.bfchain.libappmgr.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bfchain.libappmgr.model.AppInfo

object JsonUtil {
    const val TAG: String = "JsonUtil"

    /**
     * 通过 link.json 文件获取的字符串，解析成AppInfo
     */
    fun getAppInfoFromLinkJson(
        context: Context,
        content: String,
        type: FilesUtil.APP_DIR_TYPE
    ): AppInfo? {
        try {
            var gson = Gson()
            var appInfo: AppInfo = gson.fromJson(content, AppInfo::class.java)
            appInfo.isSystemApp = when (type) {
                FilesUtil.APP_DIR_TYPE.RememberApp -> false
                FilesUtil.APP_DIR_TYPE.SystemApp -> true
            }
            appInfo.iconPath = when (type) {
                FilesUtil.APP_DIR_TYPE.RememberApp -> FilesUtil.getAppIconPathName(
                    context, appInfo.bfsAppId,
                    appInfo.icon.replace("file://", ""),
                    FilesUtil.APP_DIR_TYPE.RememberApp
                )
                FilesUtil.APP_DIR_TYPE.SystemApp -> FilesUtil.getAppIconPathName(
                    context, appInfo.bfsAppId,
                    appInfo.icon.replace("file://", ""),
                    FilesUtil.APP_DIR_TYPE.SystemApp
                )
            }
            return appInfo
        } catch (e: Exception) {
            Log.d(TAG, "getAppInfoFromLinkJson e->$e")
        }
        return null
    }

    /**
     * 获取数组列表
     */
    fun getAppInfoListFromLinkJson(
        context: Context,
        content: String,
        type: FilesUtil.APP_DIR_TYPE
    ): List<AppInfo>? {
        try {
            var gson = Gson()
            var appInfos: List<AppInfo> =
                gson.fromJson(content, object : TypeToken<List<AppInfo>>() {}.type)
            appInfos.forEach {
                it.isSystemApp = when (type) {
                    FilesUtil.APP_DIR_TYPE.RememberApp -> false
                    FilesUtil.APP_DIR_TYPE.SystemApp -> true
                }
            }
            return appInfos
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }
        return null
    }
}
