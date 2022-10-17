package org.bfchain.libappmgr.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import org.bfchain.libappmgr.database.AppContract.Companion.PATH_PERMISSION
import org.bfchain.libappmgr.utils.AppContextUtil


class AppProvider : ContentProvider() {
  companion object {
    private val DATABASE_NAME = "plaoc.db"
    val DATABASE_VERSION = 1
    val PERMISSIONS_TABLE = "permissions" // 用于存储应用是否含有权限
  }

  private var mOpenHelper: AppHelper =
    AppHelper(AppContextUtil.sInstance!!, DATABASE_NAME, DATABASE_VERSION)
  private val sUriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)
  private val MATCH_PERMISSION = 1
  private val MATCH_PERMISSION_ID = 2

  private val sPermissionProjectionMap: HashMap<String, String> = hashMapOf()

  init {
    sUriMatcher.addURI(AppContract.AUTHORITY, PATH_PERMISSION, MATCH_PERMISSION)
    sUriMatcher.addURI(AppContract.AUTHORITY, "$PATH_PERMISSION/#", MATCH_PERMISSION_ID)

    sPermissionProjectionMap[AppContract.Permissions._ID] =
      PERMISSIONS_TABLE + "." + AppContract.Permissions._ID
    sPermissionProjectionMap[AppContract.Permissions.COLUMN_APP_ID] =
      PERMISSIONS_TABLE + "." + AppContract.Permissions.COLUMN_APP_ID
    sPermissionProjectionMap[AppContract.Permissions.COLUMN_PERMISSION] =
      PERMISSIONS_TABLE + "." + AppContract.Permissions.COLUMN_PERMISSION
    sPermissionProjectionMap[AppContract.Permissions.COLUMN_GRANT] =
      PERMISSIONS_TABLE + "." + AppContract.Permissions.COLUMN_GRANT
  }


  override fun onCreate(): Boolean {
    return true
  }

  override fun query(
    uri: Uri,
    projection: Array<out String>?,
    selection: String?,
    selectionArgs: Array<out String>?,
    sortOrder: String?
  ): Cursor? {
    when (sUriMatcher.match(uri)) {
      MATCH_PERMISSION -> return mOpenHelper.writableDatabase.query(
        PERMISSIONS_TABLE, projection, selection, selectionArgs, null, null, sortOrder
      )
      else -> null
    }
    return null
  }

  override fun getType(uri: Uri): String? {
    when (sUriMatcher.match(uri)) {
      MATCH_PERMISSION -> AppContract.Permissions.CONTENT_TYPE
      MATCH_PERMISSION_ID -> AppContract.Permissions.CONTENT_ITEM_TYPE
      else ->
        throw IllegalArgumentException("Unknown URI " + uri);
    }
    return null
  }

  override fun insert(uri: Uri, values: ContentValues?): Uri? {
    when (sUriMatcher.match(uri)) {
      MATCH_PERMISSION -> mOpenHelper.writableDatabase.insert(PERMISSIONS_TABLE, null, values)
      else -> null
    }
    return null
  }

  override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
    when (sUriMatcher.match(uri)) {
      MATCH_PERMISSION -> return mOpenHelper.writableDatabase.delete(
        PERMISSIONS_TABLE, selection, selectionArgs
      )
      else -> null
    }
    return 0
  }

  override fun update(
    uri: Uri,
    values: ContentValues?,
    selection: String?,
    selectionArgs: Array<out String>?
  ): Int {
    when (sUriMatcher.match(uri)) {
      MATCH_PERMISSION -> return mOpenHelper.writableDatabase.update(
        PERMISSIONS_TABLE, values, selection, selectionArgs
      )
      else -> null
    }
    return 0
  }

  class AppHelper(context: Context, db_name: String, db_version: Int) :
    SQLiteOpenHelper(context, db_name, null, db_version) {
    override fun onCreate(db: SQLiteDatabase?) {
      db?.execSQL(createPermissionTable())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
      TODO("Not yet implemented")
    }

    private fun createPermissionTable(): String {
      return "CREATE TABLE " + PERMISSIONS_TABLE + "(" +
        AppContract.Permissions._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        AppContract.Permissions.COLUMN_APP_ID + " TEXT NOT NULL," +
        AppContract.Permissions.COLUMN_PERMISSION + " TEXT NOT NULL," +
        AppContract.Permissions.COLUMN_GRANT + " INTEGER NOT NULL DEFAULT -1" + ");"
    }
  }
}
