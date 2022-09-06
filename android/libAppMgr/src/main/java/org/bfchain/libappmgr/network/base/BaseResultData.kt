package org.bfchain.libappmgr.network.base

//const val BASE_URL = "http://172.30.93.165:8080/"
const val BASE_URL = "172.30.93.165"

const val AppVersionPath = "bfs-app-1234567A/appversion.json"

typealias _ERROR = suspend (Throwable) -> Unit
typealias _PROCESS = suspend (downloadedSize: Long, length: Long, progress: Float) -> Unit
typealias _SUCCESS<T> = suspend (result: T) -> Unit

class BaseResultData<out T> constructor(val value: Any?) {
    val isSuccess: Boolean get() = value !is Failure && value !is Progress
    val isFailure: Boolean get() = value is Failure
    val isLoading: Boolean get() = value is Progress

    fun exceptionOrNull(): Throwable? =
        when (value) {
            is Failure -> value.exception
            else -> null
        }

    companion object {
        fun <T> success(value: T): BaseResultData<T> =
            BaseResultData(value)

        fun <T> failure(exception: Throwable): BaseResultData<T> =
            BaseResultData(createFailure(exception))

        fun <T> progress(currentLength: Long, length: Long, process: Float): BaseResultData<T> =
            BaseResultData(createLoading(currentLength, length, process))
    }

    data class Failure(val exception: Throwable)

    data class Progress(val currentLength: Long, val length: Long, val process: Float)

    data class BaseData<T>(val errorCode: Int, val errorMsg: String, val data: T?)
}


private fun createFailure(exception: Throwable): BaseResultData.Failure =
    BaseResultData.Failure(exception)


private fun createLoading(currentLength: Long, length: Long, process: Float) =
    BaseResultData.Progress(currentLength, length, process)


inline fun <R, T> BaseResultData<T>.fold(
    onSuccess: (value: T) -> R,
    onLoading: (loading: BaseResultData.Progress) -> R,
    onFailure: (exception: Throwable?) -> R
): R {
    return when {
        isFailure -> {
            onFailure(exceptionOrNull())
        }
        isLoading -> {
            onLoading(value as BaseResultData.Progress)
        }
        else -> {
            onSuccess(value as T)
        }
    }
}

inline fun <R> runCatching(block: () -> R): BaseResultData<R> {
    return try {
        BaseResultData.success(block())
    } catch (e: Throwable) {
        BaseResultData.failure(e)
    }
}