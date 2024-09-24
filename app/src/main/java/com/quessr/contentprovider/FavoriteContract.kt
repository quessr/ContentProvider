package com.quessr.contentprovider

import android.net.Uri

object FavoriteContract {

    const val TABLE_NAME = "favorite"
    const val AUTHORITY = "com.fbm.contentprovider"
    const val URI_STRING = "content://$AUTHORITY/$TABLE_NAME"
    val CONTENT_URI = Uri.parse(URI_STRING)
}