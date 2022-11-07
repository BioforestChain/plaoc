package info.bagen.libappmgr.ui.main

import androidx.lifecycle.ViewModel

class MainViewModel2(private val repository: MainRepository) : ViewModel() {

  suspend fun getAppVersion(path: String) = repository.getAppVersion(path)
}
