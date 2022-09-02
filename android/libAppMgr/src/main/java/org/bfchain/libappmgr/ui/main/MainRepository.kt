package org.bfchain.libappmgr.ui.main

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bfchain.libappmgr.model.AppInfo
import org.bfchain.libappmgr.model.AppVersion
import org.bfchain.libappmgr.network.ApiService
import org.bfchain.libappmgr.network.base.ApiResult
import org.bfchain.libappmgr.utils.FilesUtil
import java.io.File
import java.util.*

/**
 * @author kuky.
 * @description
 */
class MainRepository(private val api: ApiService) {

    suspend fun saveAppVersion(appInfo: AppInfo, fileName: String) {
        withContext(Dispatchers.IO) {
            /*var appVersion = api.getNewAppVersion(appInfo.bfsAppId).data
            var name = FilesUtil.simpleDateFormat.format(Date())
            FilesUtil.writeFileContent(
                fileName + File.separator + name + ".json",
                appVersion.toString()
            )*/
            var appVersion = api.getNewAppVersion(appInfo.bfsAppId)
            if (appVersion is ApiResult.IsSuccess) {
                Log.d("lin.huang", "success->$appVersion")
            } else {
                Log.d("lin.huang", "fail->$appVersion")
            }
        }
    }

    /*suspend fun showAndSaveAppVersion(appInfo: AppInfo, fileName: String): AppVersion? {
        var appVersion = withContext(Dispatchers.IO) {
            api.getNewAppVersion(appInfo.bfsAppId).data
        }
        var name = FilesUtil.simpleDateFormat.format(Date())
        FilesUtil.writeFileContent(
            fileName + File.separator + name + ".json",
            appVersion.toString()
        )
        return appVersion
    }*/
}