package com.yl.lib.privacysentry.calendar

import android.Manifest
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yl.lib.privacysentry.R
import com.yl.lib.sentry.hook.util.PrivacyLog


class CalenderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender)

        findViewById<Button>(R.id.btn_add_calendar).setOnClickListener {
            CalendarTest.Test.insert(this)
        }

        findViewById<Button>(R.id.btn_del_calendar).setOnClickListener {
            CalendarTest.Test.delete(this)
        }

        findViewById<Button>(R.id.btn_query_calendar).setOnClickListener {
            CalendarTest.Test.query(this)
        }

        findViewById<Button>(R.id.btn_edit_calendar).setOnClickListener {
            CalendarTest.Test.edit(this)
        }

        findViewById<Button>(R.id.btn_edit2_calendar).setOnClickListener {
            CalendarTest.Test.edit2(this)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CALENDAR
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(
                    Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR
                ), 1000
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            PrivacyLog.i("requestPermissions CALENDAR success")
        } else {
            PrivacyLog.i("requestPermissions CALENDAR fail")
        }
    }
}