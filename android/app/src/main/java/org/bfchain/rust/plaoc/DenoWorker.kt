package org.bfchain.rust.plaoc

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.*

private const val TAG = "DENO_WORKER"

class DenoWorker(appContext: Context, workerParams: WorkerParameters) :
  Worker(appContext, workerParams) {
  override fun doWork(): Result {
    val funName = inputData.getString(WorkerNative.WorkerName.toString())
    val data = inputData.getString("WorkerData")
    Log.i(TAG, "WorkerName=$funName,WorkerData=$data")
    if (funName !== null) {
      val calFn = ExportNative.valueOf(funName)
      callable_map[calFn]?.let { it ->
        if (data != null) {
          it(data)
        }
      }
      // 移除任务，防止重启
      App.appContext?.let { WorkManager.getInstance(it.applicationContext).cancelAllWorkByTag(funName) }
      return Result.success()
    }
    // 启动不成功就不移除了
    return Result.retry()
  }

}

/** 创建后台线程worker，来运行Service*/
fun createWorker(funName: WorkerNative, data: String = "") {
  val fnName = funName.toString()
  // 创建worker
  val denoWorkRequest: WorkRequest =
    OneTimeWorkRequestBuilder<DenoWorker>()
      .setInputData(
        Data.Builder()
          .putString(WorkerNative.WorkerName.toString(), fnName) // 添加方法名
          .putString(WorkerNative.WorkerData.toString(), data)  // 导入数据
          .build()
      )
      .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)// 加急服务，如果配额允许马上运行
      .addTag(funName.toString()) // 标记操作对象
      .build()

  // 推入处理队列
  App.appContext?.let {
    WorkManager
      .getInstance(it.applicationContext)
      .enqueue(denoWorkRequest)
  }

}
