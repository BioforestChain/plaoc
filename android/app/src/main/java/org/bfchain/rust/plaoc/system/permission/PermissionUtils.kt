package org.bfchain.rust.plaoc.system.permission


import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import org.bfchain.rust.plaoc.App
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class PermissionUtils(private var context: Context) {

  @Synchronized
  fun isPermissionGranted(permission: String): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
      return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    val hasPermission = ContextCompat.checkSelfPermission(context, permission)
    return hasPermission == PackageManager.PERMISSION_GRANTED
  }

  fun openAppSettings() {
    val i = Intent()
    i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    i.addCategory(Intent.CATEGORY_DEFAULT)
    i.data = Uri.parse("package:" + context.packageName)
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    context.startActivity(i)
  }

  /**
   * 打开配置通知的权限
   */
  @SuppressLint("ObsoleteSdkInt")
  fun getNotificationPermission() {
    var localIntent = Intent()
    //直接跳转到应用通知设置的代码：
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0及以上
      localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS";
      localIntent.data = Uri.fromParts("package", context.packageName, null);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上到8.0以下
      localIntent.action = "android.settings.APP_NOTIFICATION_SETTINGS";
      localIntent.putExtra("app_package", context.packageName);
      localIntent.putExtra("app_uid", context.applicationInfo.uid);
    } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {//4.4
      localIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
      localIntent.addCategory(Intent.CATEGORY_DEFAULT);
      localIntent.data = Uri.parse("package:${context.packageName}");
    } else {
      //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
      localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      if (Build.VERSION.SDK_INT >= 9) {
        localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS";
        localIntent.data = Uri.fromParts("package", context.packageName, null);
      } else if (Build.VERSION.SDK_INT <= 8) {
        localIntent.action = Intent.ACTION_VIEW;
        localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
        localIntent.putExtra("com.android.settings.ApplicationPkgName", context.packageName);
      }
    }
    context.startActivity(localIntent);
  }

  /**
   * 判断当前app是否有开通Notification
   */
  @SuppressLint("NewApi")
  fun isNotificationEnabled(): Boolean {
    val mAppOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val pkg = context.applicationContext.packageName
    val uid = context.applicationInfo.uid
    var appOpsClass: Class<*>? = null
    /* Context.APP_OPS_MANAGER */try {
      appOpsClass = Class.forName(AppOpsManager::class.java.name)
      val checkOpNoThrowMethod: Method = appOpsClass.getMethod(
        "checkOpNoThrow", Integer.TYPE, Integer.TYPE,
        String::class.java
      )
      val opPostNotificationValue: Field = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION")
      val value = opPostNotificationValue.get(Int::class.java) as Int
      return checkOpNoThrowMethod.invoke(
        mAppOps, value, uid, pkg
      ) as Int == AppOpsManager.MODE_ALLOWED
    } catch (e: ClassNotFoundException) {
      e.printStackTrace()
    } catch (e: NoSuchMethodException) {
      e.printStackTrace()
    } catch (e: NoSuchFieldException) {
      e.printStackTrace()
    } catch (e: InvocationTargetException) {
      e.printStackTrace()
    } catch (e: IllegalAccessException) {
      e.printStackTrace()
    }
    return false
  }
}
