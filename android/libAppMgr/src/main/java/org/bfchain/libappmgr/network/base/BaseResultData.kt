package org.bfchain.libappmgr.network.base

import java.io.File

/*data class BaseResultData<T>(
    val errorCode: Int,
    val errorMsg: String?,
    val `data`: T?
)*/

sealed class ApiResult<T>() {
    data class IsSuccess<T>(val errorCode: Int, val errorMsg: String, val data: T) : ApiResult<T>()
    data class IsError(val errorCode: Int, val errorMsg: String) : ApiResult<Nothing>()
    data class OtherError(val throwable: Throwable) : ApiResult<Nothing>()
}

typealias download_error = suspend (Throwable) -> Unit
typealias download_process = suspend (downloadedSize: Long, length: Long, progress: Float) -> Unit
typealias download_success = suspend (uri: File) -> Unit