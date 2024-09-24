package com.quessr.contentprovider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.quessr.contentprovider.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var contactRequestLauncher: ActivityResultLauncher<Intent>
    private lateinit var calendarRequestLauncher: ActivityResultLauncher<Intent>

    private lateinit var contentResolverHelper: ContentResolverHelper

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contentResolverHelper = ContentResolverHelper(this)

//        contentResolverHelper.insertItem("Sample Photo", "This is a sample photo.")

        contentResolverHelper.getAllItems()

//        val status = ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS")
//        if (status == PackageManager.PERMISSION_GRANTED) {
//            Log.d("test", "contact permission // granted")
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf<String>("android.permission.READ_CONTACTS"),
//                100
//            )
//            Log.d("test", "contact permission // denied")
//        }
//
//        val calStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
//        if (calStatus == PackageManager.PERMISSION_GRANTED) {
//            Log.d("test", "calendar permission / granted")
//        } else {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALENDAR), 200)
//            Log.d("test", "calendar permission / denied")
//        }

        // 연락처 권한 요청
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 100)
        }

        // 캘린더 권한 요청
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALENDAR), 200)
        }

        // 연락처
        contactRequestLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val cursor = contentResolver.query(
                        it.data!!.data!!,
                        arrayOf<String>(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ),
                        null,
                        null,
                        null
                    )
                    Log.d("test", "cursor size : ${cursor?.count}")

                    if (cursor!!.moveToFirst()) {
                        val name = cursor.getString(0)
                        val phone = cursor.getString(1)
                        binding.tvContractInfo.text = "name: $name, \nphone: $phone"
                    }
                }
            }

        calendarRequestLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val eventUri = it.data?.data
                    if (eventUri != null) {
                        val cursor = contentResolver.query(
                            eventUri,
                            arrayOf(
                                CalendarContract.Events._ID,
                                CalendarContract.Events.TITLE,
                                CalendarContract.Events.DTSTART,
                                CalendarContract.Events.DTEND
                            ), null, null, null
                        )

                        cursor?.use {
                            while (it.moveToNext()) {
                                val id = it.getLong(it.getColumnIndex(CalendarContract.Events._ID))
                                val title =
                                    it.getString(it.getColumnIndex(CalendarContract.Events.TITLE))
                                val startTime =
                                    it.getLong(it.getColumnIndex(CalendarContract.Events.DTSTART))
                                val endTime =
                                    it.getLong(it.getColumnIndex(CalendarContract.Events.DTEND))

                                Log.d(
                                    "CalendarEvent",
                                    "ID: $id, Title: $title, Start: $startTime, End: $endTime"
                                )
                            }
                        }

                    }
                }
            }

        binding.btnOpenContract.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            contactRequestLauncher.launch(intent)
        }

        binding.btnOpenCalendar.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CALENDAR
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("btnOpenCalender", "btnOpenCalender")
                CalendarManager.getEvents(this) { events ->
                    binding.tvCalendarInfo.text = events
                    Log.d("btnOpenCalender@@@", "events $events")
                }
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CALENDAR),
                    200
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("test", "Contact permission granted")
                } else {
                    Log.d("test", "Contact permission denied")
                }
            }

            200 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("test", "Calendar permission granted")
                } else {
                    Log.d("test", "Calendar permission denied")
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun queryCalendarEvents() {
        val cursor = contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            arrayOf(
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND
            ), null, null, null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndex(CalendarContract.Events._ID))
                val title = it.getString(it.getColumnIndex(CalendarContract.Events.TITLE))
                val startTime = it.getLong(it.getColumnIndex(CalendarContract.Events.DTSTART))
                val endTime = it.getLong(it.getColumnIndex(CalendarContract.Events.DTEND))

                Log.d("CalendarEvent", "ID: $id, Title: $title, Start: $startTime, End: $endTime")
            }
        }
    }
}