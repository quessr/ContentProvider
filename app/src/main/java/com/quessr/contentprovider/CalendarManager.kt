package com.quessr.contentprovider

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import java.time.LocalDateTime
import android.provider.CalendarContract
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CalendarManager {

    companion object {
        private const val DEBUG_TAG = "CalendarManager"

        @SuppressLint("Range")
        fun getEvents(context: Context, callback: (String) -> Unit) {
            Log.d(DEBUG_TAG, "CalendarManager getEvents called")

            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CALENDAR
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(DEBUG_TAG, "Permission denied to READ_CALENDAR - requesting it")
                // 권한 요청은 MainActivity에서 처리해야 합니다.
                return
            }

            val contentResolver = context.contentResolver
            val calendarUri = CalendarContract.Events.CONTENT_URI
            val projection = arrayOf(
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND
            )

            // 날짜 범위를 설정합니다. 현재 시점 기준으로 30일 전과 후로 설정합니다.
            val now = System.currentTimeMillis()
            val startTime = now - 24 * 60 * 60 * 1000L * 30 // 30일 전
            val endTime = now + 24 * 60 * 60 * 1000L * 30 // 30일 후

            val selection =
                "${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTEND} <= ?"
            val selectionArgs = arrayOf(startTime.toString(), endTime.toString())

            val cursor: Cursor? = contentResolver.query(
                calendarUri,
                projection,
                selection,
                selectionArgs,
                null
            )

            val events = StringBuilder()
            cursor?.use {
                while (it.moveToNext()) {
                    val eventId = it.getLong(it.getColumnIndex(CalendarContract.Events._ID))
                    val title = it.getString(it.getColumnIndex(CalendarContract.Events.TITLE))
                    val startTime = it.getLong(it.getColumnIndex(CalendarContract.Events.DTSTART))
                    val endTime = it.getLong(it.getColumnIndex(CalendarContract.Events.DTEND))

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val start = dateFormat.format(Date(startTime))
                    val end = dateFormat.format(Date(endTime))
                    events.append("ID: $eventId\nTitle: $title\nStart: $start\nEnd: $end\n\n")
                    Log.d(
                        DEBUG_TAG,
                        "ID: $eventId, Title: $title, Start: $startTime, End: $endTime"
                    )
                }
            }
            Log.d(DEBUG_TAG, "Events fetched: $events")
            callback(events.toString())
        }
    }

//    companion object {
//        private const val PERMISSION_REQUEST_CODE = 1
//        private const val DEBUG_TAG = "CalendarManager"
//
//        private val INSTANCE_PROJECTION = arrayOf(
//            CalendarContract.Instances.EVENT_ID,
//            CalendarContract.Instances.BEGIN,
//            CalendarContract.Instances.TITLE,
//            CalendarContract.Instances.END
//        )
//
//        private const val PROJECTION_ID_INDEX = 0
//        private const val PROJECTION_BEGIN_INDEX = 1
//        private const val PROJECTION_TITLE_INDEX = 2
//        private const val PROJECTION_END_INDEX = 3
//
//        @SuppressLint("Range")
//        fun getEvents(context: Context, callback: (String) -> Unit) {
//            Log.d("CalendarManager","CalendarManager")
//
//            val contentResolver = context.contentResolver
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val startTime =
//                    LocalDateTime.now().minusMonths(24).toEpochSecond(OffsetDateTime.now().offset)
//                val endTime =
//                    LocalDateTime.now().plusHours(24).toEpochSecond(OffsetDateTime.now().offset)
//
//                val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
//                ContentUris.appendId(builder, startTime)
//                ContentUris.appendId(builder, endTime)
//
//                if (ContextCompat.checkSelfPermission(
//                        context,
//                        Manifest.permission.READ_CALENDAR
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    Log.d("CalendarManager", "permission denied to READ_CALENDAR - requesting it")
//                    ActivityCompat.requestPermissions(
//                        context as Activity,
//                        arrayOf(Manifest.permission.READ_CALENDAR),
//                        PERMISSION_REQUEST_CODE
//                    )
//
//                } else {
//                    Log.d("Content URI", builder.build().toString())
//                    val cursor: Cursor? = contentResolver.query(
//                        builder.build(),
//                        INSTANCE_PROJECTION,
//                        null,
//                        null,
//                        null
//                    )
//
//                    val events = StringBuilder()
//                    cursor?.use {
//                        while (it.moveToNext()) {
//                            val eventId = it.getLong(it.getColumnIndex(CalendarContract.Events._ID))
//                            val title = it.getString(it.getColumnIndex(CalendarContract.Events.TITLE))
//                            val startTime = it.getLong(it.getColumnIndex(CalendarContract.Events.DTSTART))
//                            val endTime = it.getLong(it.getColumnIndex(CalendarContract.Events.DTEND))
//
//                            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//                            val start = dateFormat.format(Date(startTime))
//                            val end = dateFormat.format(Date(endTime))
//                            events.append("ID: $eventId\nTitle: $title\nStart: $start\nEnd: $end\n\n")
//                            Log.d(DEBUG_TAG, "ID: $eventId, Title: $title, Start: $startTime, End: $endTime")
//                        }
//                    }
//                    callback(events.toString())
//                }
//
//            } else {
//            }
//        }
//
//    }
}