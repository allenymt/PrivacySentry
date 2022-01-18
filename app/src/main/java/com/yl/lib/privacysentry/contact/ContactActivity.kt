package com.yl.lib.privacysentry.contact

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yl.lib.privacysentry.R
import com.yl.lib.privacysentry.calendar.CalendarTest
import com.yl.lib.sentry.hook.util.PrivacyLog

class ContactActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        findViewById<Button>(R.id.btn_get_all_contacts).setOnClickListener {
            ContactManager.Manager.testGetAllContact(this)
        }

        findViewById<Button>(R.id.btn_add_contact1).setOnClickListener {
            ContactManager.Manager.testInsert(this)
        }

        findViewById<Button>(R.id.btn_add_contact2).setOnClickListener {
            ContactManager.Manager.testSave(this)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CONTACTS
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_CALL_LOG
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
            PrivacyLog.i("requestPermissions CONTACTS success")
        } else {
            PrivacyLog.i("requestPermissions CONTACTS fail")
        }
    }
}