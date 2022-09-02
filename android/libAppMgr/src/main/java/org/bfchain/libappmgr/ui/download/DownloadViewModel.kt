package org.bfchain.libappmgr.ui.download

import androidx.lifecycle.ViewModel
import org.bfchain.libappmgr.network.base.download_error
import org.bfchain.libappmgr.network.base.download_process
import org.bfchain.libappmgr.network.base.download_success

class DownloadViewModel(private val repository: DownloadRepository) : ViewModel() {

    suspend fun downloadApp(
        url: String, outputFile: String,
        onError: download_error = {},
        onProcess: download_process = { _, _, _ -> },
        onSuccess: download_success = { }
    ) {
        repository.downloadApp(url, outputFile, onError, onProcess, onSuccess)
    }
}