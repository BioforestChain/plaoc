package org.bfchain.rust.plaoc

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

private const val TAG = "DENO_WORKER"
var ordData = "";

class DenoWorker(appContext: Context, workerParams: WorkerParameters) :
  Worker(appContext, workerParams) {
  override fun doWork(): Result {
    val funName = inputData.getString(WorkerNative.WorkerName.toString())
    val data = inputData.getString("WorkerData")
    Log.i(TAG, "WorkerName=$funName,WorkerData=$data")
    if (funName !== null) {
      val calFn = ExportNative.valueOf(funName)
      // TODO 这样处理是不得已为之，因为denoRuntime在主线程起会导致无响应,而现在使用WorkManager处理的话是可以运行的。
      // TODO 但是测试多次打开关闭后发现WorkManager状态一直没有结束，而是卡在了`it(data)`这里(卡在了runtime运行时).await。
      // TODO 所以只能再加一个线程，主动引发报错，让代码走到外部的success,从而释放一个后台线程的状态。
      try {
        thread {
          callable_map[calFn]?.let { it ->
            if (data != null) {
              it(data)
            }
          }
        }.start()
      } catch (e:Exception) {
        e.printStackTrace()
        return Result.failure()
      }
    }
    return Result.success()
  }
}

/** 创建后台线程worker，来运行Service*/
fun createWorker(funName: WorkerNative, data: String = "") {
  val fnName = funName.toString()
  val done = WorkManager.getInstance(App.appContext.applicationContext).getWorkInfosByTag(fnName).isDone
    Log.i("xx","workManager=> $done")
  if(done) {
    return
  }
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
  WorkManager
    .getInstance(App.appContext)
    .enqueue(denoWorkRequest)
}

//  WorkManager.getInstance(App.appContext.applicationContext).enqueueUniquePeriodicWork(
//    fnName,
//    ExistingPeriodicWorkPolicy.KEEP, request
//  )
