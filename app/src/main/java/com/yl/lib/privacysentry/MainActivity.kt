package com.yl.lib.privacysentry

import android.Manifest
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yl.lib.privacy_test.PrivacyProxySelfTest2
import com.yl.lib.privacy_test.TestMethod
import com.yl.lib.privacy_test.TestMethodInJava
import com.yl.lib.privacysentry.calendar.CalenderActivity
import com.yl.lib.privacysentry.contact.ContactActivity
import com.yl.lib.privacysentry.location.LocationTestActivity
import com.yl.lib.privacysentry.process.MultiProcessB
import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.util.MainProcessUtil
import com.yl.lib.sentry.hook.util.PrivacyLog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_androidId).setOnClickListener {
            var androidId = PrivacyMethod.PrivacyMethod.getAndroidId(this)
            TestMethodInJava.getAndroidId(this)
            TestMethodInJava.getAndroidIdSystem(this)
            PrivacyLog.i("androidId is $androidId")
        }


        findViewById<Button>(R.id.btn_deviceId).setOnClickListener {
            var deviceId = PrivacyMethod.PrivacyMethod.getDeviceId(this)
            PrivacyLog.i("deviceId is $deviceId")

            var deviceId1 = PrivacyMethod.PrivacyMethod.getDeviceId1(this)
            PrivacyLog.i("deviceId is $deviceId1")

            PrivacyLog.i("deviceId is ${PrivacyMethod.PrivacyMethod.getMeid(this)}")
        }

        findViewById<Button>(R.id.btn_mac_address).setOnClickListener {
            var macRaw = PrivacyMethod.PrivacyMethod.getMacRaw(this)
            PrivacyLog.i("macRaw is $macRaw")
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

        findViewById<Button>(R.id.btn_imsi).setOnClickListener {
            var imsi = PrivacyMethod.PrivacyMethod.getIMSI(this)
            PrivacyLog.i("imsi is $imsi")
        }

        findViewById<Button>(R.id.btn_installed_packages).setOnClickListener {
            var privacySentryInstalled =
                PrivacyMethod.PrivacyMethod.isInstalled(application, "com.yl.lib.privacysentry123")
            PrivacyLog.i("privacySentryInstalled is $privacySentryInstalled")

            var privacySentryInstalled2 =
                PrivacyMethod.PrivacyMethod.isInstalled2(
                    application,
                    this,
                    "com.yl.lib.privacysentry123"
                )
            PrivacyLog.i("privacySentryInstalled2 is $privacySentryInstalled2")

            PrivacyLog.i(
                "privacySentryInstalled2 is ${
                    PrivacyMethod.PrivacyMethod.queryActivityInfo(
                        application,
                        this
                    )
                }"
            )
        }

        findViewById<Button>(R.id.btn_test_cms).setOnClickListener {
            PrivacyMethod.PrivacyMethod.testHookCms(application)
            PrivacyLog.i("testHookCms")
        }

        findViewById<Button>(R.id.btn_test_ams_process).setOnClickListener {
            PrivacyMethod.PrivacyMethod.testRunningProcess(application)
            PrivacyMethod.PrivacyMethod.testRunningTask(application)
        }

        findViewById<Button>(R.id.btn_main_process).setOnClickListener {
            var mainProcess = MainProcessUtil.MainProcessChecker.isMainProcess(this)
            var currentProcessName = MainProcessUtil.MainProcessChecker.getProcessName(this)
            PrivacyLog.i("mainProcess currentProcessName is $currentProcessName  is $mainProcess")

            PrivacyProxyCallJava.getRunningTasks(
                getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager,
                1
            )
            PrivacyProxySelfTest2.getRunningTasks456(
                getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager,
                1
            )
        }


        findViewById<Button>(R.id.btn_force_finish).setOnClickListener {
            PrivacySentry.Privacy.stop()
        }

        findViewById<Button>(R.id.btn_test_processB).setOnClickListener {
            startService(Intent(this, MultiProcessB::class.java))
        }

        findViewById<Button>(R.id.btn_test_lib_method).setOnClickListener {
            TestMethod.PrivacyMethod.getDeviceId(applicationContext)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TestMethod.PrivacyMethod.getDeviceId1(applicationContext)
            }
            TestMethod.PrivacyMethod.getICCID(applicationContext)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TestMethod.PrivacyMethod.getIMEI(applicationContext)
            }
            TestMethod.PrivacyMethod.getIMSI(applicationContext)
            TestMethod.PrivacyMethod.testHookCms(applicationContext)
            TestMethod.PrivacyMethod.testRunningProcess(applicationContext)
            TestMethod.PrivacyMethod.testRunningTask(applicationContext)
        }


        // 变量hook测试
        findViewById<Button>(R.id.btn_test_sn).setOnClickListener {
            var sn = PrivacyMethod.PrivacyMethod.getSerial()
            var brand = android.os.Build.BRAND
            PrivacyLog.i("Android SN is $sn brand is $brand")
        }


        findViewById<Button>(R.id.btn_to_calender).setOnClickListener {
            startActivity(Intent(this, CalenderActivity::class.java))
        }

        findViewById<Button>(R.id.btn_to_contact).setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }

        findViewById<Button>(R.id.btn_test_sensor).setOnClickListener {
            PrivacyMethod.PrivacyMethod.testSensor(this)
        }

        findViewById<Button>(R.id.btn_thread_cache).setOnClickListener {
            for (index in 1..20) {
                Thread(Thread.currentThread().threadGroup, object : Runnable {
                    override fun run() {
                        var result = PrivacyMethod.PrivacyMethod.getAndroidId(this@MainActivity)
                        PrivacyLog.e("btn_thread_cache result is $result")

                        PrivacyMethod.PrivacyMethod.getDeviceId(this@MainActivity)

                        PrivacyMethod.PrivacyMethod.getDeviceId1(this@MainActivity)

                        PrivacyMethod.PrivacyMethod.getICCID(this@MainActivity)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            PrivacyMethod.PrivacyMethod.getIMEI(this@MainActivity)
                        }

                        PrivacyMethod.PrivacyMethod.getIMSI(this@MainActivity)

                        PrivacyMethod.PrivacyMethod.getMacRaw(this@MainActivity)

                        PrivacyMethod.PrivacyMethod.getMacV2()

                        PrivacyMethod.PrivacyMethod.getMeid(this@MainActivity)

                        PrivacyMethod.PrivacyMethod.getSerial()
                    }
                }, "test_thread_$index", 0).start()
            }
        }


        findViewById<Button>(R.id.btn_to_location).setOnClickListener {
            startActivity(Intent(this, LocationTestActivity::class.java))
        }

        //Android Q开始，READ_PHONE_STATE 不再有用，不用全局弹框
        var permissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 1000)
        }

        AlertDialog.Builder(this).setMessage("确认隐私协议").setPositiveButton(
            "确定"
        ) { dialog, which ->
            PrivacySentry.Privacy.updatePrivacyShow()
            PrivacySentry.Privacy.closeVisitorModel()
        }.create().show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            PrivacyLog.i("requestPermissions ${Manifest.permission.READ_PHONE_STATE} success")
        } else {
            PrivacyLog.i("requestPermissions ${Manifest.permission.READ_PHONE_STATE} fail")
        }
    }
}