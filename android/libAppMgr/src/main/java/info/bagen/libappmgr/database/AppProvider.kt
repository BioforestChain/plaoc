package info.bagen.libappmgr.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import info.bagen.libappmgr.database.AppContract.Companion.PATH_MEDIA
import info.bagen.libappmgr.database.AppContract.Companion.PATH_PERMISSION
import info.bagen.libappmgr.utils.AppContextUtil


class AppProvider : ContentProvider() {
  companion object {
    private val DATABASE_NAME = "plaoc.db"
    val DATABASE_VERSION = 2
    val PERMISSIONS_TABLE = "permissions" // 用于存储应用是否含有权限
    val MEDIAS_TABLE = "medias" // 用于存储照片和视频信息
  }

  private var mOpenHelper: AppHelper =
    AppHelper(AppContextUtil.sInstance!!, DATABASE_NAME, DATABASE_VERSION)
  private val sUriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)
  private val MATCH_PERMISSION = 1
  private val MATCH_PERMISSION_ID = 2
  private val MATCH_MDEIA = 3
  private val MATCH_MDEIA_ID = 4

  private val sProjectionMap: HashMap<String, String> = hashMapOf()

  init {
    sUriMatcher.addURI(AppContract.AUTHORITY, PATH_PERMISSION, MATCH_PERMISSION)
    sUriMatcher.addURI(AppContract.AUTHORITY, "$PATH_PERMISSION/#", MATCH_PERMISSION_ID)
    sUriMatcher.addURI(AppContract.AUTHORITY, PATH_MEDIA, MATCH_MDEIA)
    sUriMatcher.addURI(AppContract.AUTHORITY, "$PATH_MEDIA/#", MATCH_MDEIA_ID)

    sProjectionMap[AppContract.Permissions.COLUMN_ID] =
      PERMISSIONS_TABLE + "." + AppContract.Permissions.COLUMN_ID
    sProjectionMap[AppContract.Permissions.COLUMN_APP_ID] =
      PERMISSIONS_TABLE + "." + AppContract.Permissions.COLUMN_APP_ID
    sProjectionMap[AppContract.Permissions.COLUMN_NAME] =
      PERMISSIONS_TABLE + "." + AppContract.Permissions.COLUMN_NAME
    sProjectionMap[AppContract.Permissions.COLUMN_GRANT] =
      PERMISSIONS_TABLE + "." + AppContract.Permissions.COLUMN_GRANT

    sProjectionMap[AppContract.Medias.COLUMN_ID] =
      PERMISSIONS_TABLE + "." + AppContract.Medias.COLUMN_ID
    sProjectionMap[AppContract.Medias.COLUMN_PATH] =
      PERMISSIONS_TABLE + "." + AppContract.Medias.COLUMN_PATH
    sProjectionMap[AppContract.Medias.COLUMN_TYPE] =
      PERMISSIONS_TABLE + "." + AppContract.Medias.COLUMN_TYPE
    sProjectionMap[AppContract.Medias.COLUMN_TIME] =
      PERMISSIONS_TABLE + "." + AppContract.Medias.COLUMN_TIME
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
      MATCH_MDEIA -> AppContract.Medias.CONTENT_TYPE
      MATCH_MDEIA_ID -> AppContract.Medias.CONTENT_ITEM_TYPE
      else ->
        throw IllegalArgumentException("Unknown URI " + uri);
    }
    return null
  }

  override fun insert(uri: Uri, values: ContentValues?): Uri? {
    when (sUriMatcher.match(uri)) {
      MATCH_PERMISSION -> mOpenHelper.writableDatabase.insert(PERMISSIONS_TABLE, null, values)
      MATCH_MDEIA -> mOpenHelper.writableDatabase.insert(MEDIAS_TABLE, null, values)
      else -> null
    }
    return null
  }

  override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
    when (sUriMatcher.match(uri)) {
      MATCH_PERMISSION -> return mOpenHelper.writableDatabase.delete(
        PERMISSIONS_TABLE, selection, selectionArgs
      )
      MATCH_MDEIA -> return mOpenHelper.writableDatabase.delete(
        MEDIAS_TABLE, selection, selectionArgs
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
      db?.execSQL(createMediaTable())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
      if (oldVersion == 1 && newVersion == 2) {
        db?.execSQL(createMediaTable())
      }
    }

    private fun createPermissionTable(): String {
      return "CREATE TABLE IF NOT EXISTS " + PERMISSIONS_TABLE + "(" +
        AppContract.Permissions.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        AppContract.Permissions.COLUMN_APP_ID + " TEXT NOT NULL," +
        AppContract.Permissions.COLUMN_NAME + " TEXT NOT NULL," +
        AppContract.Permissions.COLUMN_GRANT + " INTEGER NOT NULL DEFAULT -1" + ");"
    }

    private fun createMediaTable(): String {
      return "CREATE TABLE IF NOT EXISTS " + MEDIAS_TABLE + "(" +
        AppContract.Medias.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        AppContract.Medias.COLUMN_PATH + " TEXT NOT NULL," +
        AppContract.Medias.COLUMN_TYPE + " TEXT NOT NULL," +
        AppContract.Medias.COLUMN_TIME + " INTEGER NOT NULL DEFAULT 0" + ")" +
        "ADD INDEX ind_path(${AppContract.Medias.COLUMN_PATH}, ind_type(${AppContract.Medias.COLUMN_TYPE});"
    }
  }
}
