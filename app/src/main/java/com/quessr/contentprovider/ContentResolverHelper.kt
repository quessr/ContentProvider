package com.quessr.contentprovider

import android.annotation.SuppressLint
import android.content.ClipDescription
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi

class ContentResolverHelper(private val context: Context) {

    private val contentResolver: ContentResolver = context.contentResolver

    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllItems() {
        val cursor = contentResolver.query(FavoriteContract.CONTENT_URI, null, null, null)

        try {
            cursor?.use {
                if (it.count > 0) {
                    while (it.moveToNext()) {
                        val idIndex = it.getColumnIndex("id")
                        val titleIndex = it.getColumnIndex("title")
                        val descriptionIndex = it.getColumnIndex("description")

                        val id = it.getString(idIndex)
                        val title = it.getString(titleIndex)
                        val description = it.getString(descriptionIndex)

                        Log.d(">>>", "@# id[$id] title[$title] description[$description]")
                    }
                } else {
                    Log.d(">>>", "No items found.")
                }
            }
        } catch (e: Exception) {
            Log.e(">>>", "Error querying items: ${e.message}")
        }
    }

    fun getItem(id: String) {
        val cursor =
            contentResolver.query(FavoriteContract.CONTENT_URI, null, "id=?", arrayOf(id), null)

        cursor?.use {
            if (it.count > 0) {
                while (it.moveToNext()) {
                    val idIndex = it.getColumnIndex("id")
                    val titleIndex = it.getColumnIndex("title")
                    val descriptionIndex = it.getColumnIndex("description")

                    val itemId = it.getString(idIndex)
                    val title = it.getString(titleIndex)
                    val description = it.getString(descriptionIndex)

                    Log.v(">>>", "@# id[$itemId] title[$title] description[$description]")
                }
            }
        }
    }

    fun insertItem(title: String, description: String) {
        val contentValue = ContentValues().apply {
            put("title", title)
            put("description", description)
            put("id", System.currentTimeMillis().toString())
        }
        contentResolver.insert(FavoriteContract.CONTENT_URI, contentValue)
    }

    fun removeItem(id: String) {
        contentResolver.delete(FavoriteContract.CONTENT_URI, "id=?", arrayOf(id))
    }

    fun customMethodGetId(): String? {
        var value: String? = null
        val bundle: Bundle? =
            contentResolver.call(FavoriteContract.CONTENT_URI, "getId", null, null)

        bundle?.let {
            value = it.getString("id")
            Log.v(">>>", "customMethodGetId : $value")
        }
        return value
    }
}