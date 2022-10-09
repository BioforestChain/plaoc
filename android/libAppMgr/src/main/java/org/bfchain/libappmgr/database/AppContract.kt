package org.bfchain.libappmgr.database

import android.net.Uri


class AppContract {
  companion object {
    const val AUTHORITY = "org.bfchain.rust.plaoc"
    const val PATH_PERMISSION = "permission"
  }

  object Permissions {
    val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH_PERMISSION");

    const val CONTENT_TYPE = "vnd.android.cursor.dir/permission"
    const val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/permission"

    const val _ID: String = "_id"
    const val COLUMN_APP_ID: String = "app_id"
    const val COLUMN_PERMISSION: String = "permission"
    const val COLUMN_GRANT: String = "grant"
  }
}
