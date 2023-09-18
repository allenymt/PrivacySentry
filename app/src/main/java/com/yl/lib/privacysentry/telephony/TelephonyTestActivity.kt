package com.yl.lib.privacysentry.telephony

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.yl.lib.privacysentry.R
import com.yl.lib.privacysentry.test.PrivacyMethod
import com.yl.lib.sentry.hook.util.PrivacyLog

class TelephonyTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_telephony_test)

        findViewById<Button>(R.id.btn_deviceId).setOnClickListener {
            var deviceId = PrivacyMethod.PrivacyMethod.getDeviceId(this)
            PrivacyLog.i("deviceId is $deviceId")

            var deviceId1 = PrivacyMethod.PrivacyMethod.getDeviceId1(this)
            PrivacyLog.i("deviceId is $deviceId1")

            PrivacyLog.i("deviceId is ${PrivacyMethod.PrivacyMethod.getMeid(this)}")
        }


        findViewById<Button>(R.id.btn_iccid).setOnClickListener {
            var iccid = PrivacyMethod.PrivacyMethod.getICCID(this)
            PrivacyLog.i("iccid is $iccid")
        }

        findViewById<Button>(R.id.btn_imei).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var imei = PrivacyMethod.PrivacyMethod.getIMEI(this)
                PrivacyLog.i("imei is $imei")
            }
        }

        findViewById<Button>(R.id.btn_sim_operator).setOnClickListener {
            var simOperator = PrivacyMethod.PrivacyMethod.getSimOperator(this)
            PrivacyLog.i("simOperator is $simOperator")

            var networkOperator = PrivacyMethod.PrivacyMethod.getNetworkOperator(this)
            PrivacyLog.i("networkOperator is $networkOperator")
        }

        findViewById<Button>(R.id.btn_sim_state).setOnClickListener {
            var simOperator = PrivacyMethod.PrivacyMethod.getSimState(this)
            PrivacyLog.i("simOperator is $simOperator")
        }

        //Android Q开始，READ_PHONE_STATE 不再有用，不用全局弹框
        var permissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 1000)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            PrivacyLog.i("requestPermissions ${permissions[0]} success")
        } else {
            PrivacyLog.i("requestPermissions ${permissions[0]} fail")
        }
    }
}