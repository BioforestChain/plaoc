package org.bfchain.libappmgr.ui.main

import androidx.lifecycle.ViewModel
import org.bfchain.libappmgr.model.AppInfo

class MainViewModel(private val repository: MainRepository) : ViewModel() {

    /*suspend fun showAndSaveAppVersion(appInfo: AppInfo, fileName: String) = flow {
        emit(repository.showAndSaveAppVersion(appInfo, fileName))
    }*/

    suspend fun saveAppVersion(appInfo: AppInfo, fileName: String) {
        repository.saveAppVersion(appInfo, fileName)
    }
}