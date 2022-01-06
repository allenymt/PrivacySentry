package com.yl.lib.privacysentry

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yl.lib.privacy_test.TestMethod
import com.yl.lib.privacysentry.process.MultiProcessB
import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.excel.ExcelBuildDataListener
import com.yl.lib.sentry.hook.excel.ExcelUtil
import com.yl.lib.sentry.hook.printer.PrivacyFunBean
import com.yl.lib.sentry.hook.util.MainProcessUtil
import com.yl.lib.sentry.hook.util.PrivacyLog
import com.yl.lib.sentry.hook.util.PrivacyUtil
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_androidId).setOnClickListener {
            var androidId = PrivacyMethod.PrivacyMethod.getAndroidId(this)
            PrivacyLog.i("androidId is $androidId")
        }


        findViewById<Button>(R.id.btn_deviceId).setOnClickListener {
            var deviceId = PrivacyMethod.PrivacyMethod.getDeviceId(this)
            PrivacyLog.i("deviceId is $deviceId")

            var deviceId1 = PrivacyMethod.PrivacyMethod.getDeviceId1(this)
            PrivacyLog.i("deviceId is $deviceId1")

            PrivacyLog.i("deviceId is ${PrivacyMethod.PrivacyMethod.getMeid(this)}")

            PrivacyProxyCallJava.getRunningTasks(
                getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager,
                1
            )
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
        }

        findViewById<Button>(R.id.btn_export_excel).setOnClickListener {
            exportExcel(this, "$externalCacheDir${File.separator}testExcel")
        }

        findViewById<Button>(R.id.btn_force_finish).setOnClickListener {
            PrivacySentry.Privacy.stopWatch()
        }

        findViewById<Button>(R.id.btn_mock_privacy_click).setOnClickListener {
            PrivacySentry.Privacy.updatePrivacyShow()
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
            PrivacyLog.i("requestPermissions ${Manifest.permission.READ_PHONE_STATE} success")
        } else {
            PrivacyLog.i("requestPermissions ${Manifest.permission.READ_PHONE_STATE} fail")
        }
    }

    private fun exportExcel(context: Context, filePath: String) {
        val file: File = File(filePath)
        if (!file.exists()) {
            file.mkdirs()
        }
        val excelFileName = "/demo.xls"
        val title = arrayOf("别名", "函数名", "调用堆栈", "调用次数")
        val sheetName = "demoSheetName"
        val privacyFunBeanList: MutableList<PrivacyFunBean> = ArrayList<PrivacyFunBean>()
        val demoBean1 = PrivacyFunBean(
            "imei",
            "getImei",
            PrivacyUtil.Util.getStackTrace() ?: "",
            10
        )
        val demoBean2 = PrivacyFunBean(
            "imsi",
            "getImei",
            PrivacyUtil.Util.getStackTrace() ?: "",
            1
        )
        val demoBean3 = PrivacyFunBean(
            "device",
            "device",
            PrivacyUtil.Util.getStackTrace() ?: "",
            2
        )
        val demoBean4 = PrivacyFunBean(
            "install",
            "install",

            PrivacyUtil.Util.getStackTrace() ?: "",
            0
        )
        privacyFunBeanList.add(demoBean1)
        privacyFunBeanList.add(demoBean2)
        privacyFunBeanList.add(demoBean3)
        privacyFunBeanList.add(demoBean4)
        var filePathNew = filePath + excelFileName
        var sheetIndex = 0
        ExcelUtil.instance.initExcel(
            filePathNew,
            arrayListOf(sheetName),
            arrayListOf(title),
            arrayListOf(sheetIndex)
        )
        ExcelUtil.instance.writeObjListToExcel(privacyFunBeanList, filePathNew, sheetIndex, object :
            ExcelBuildDataListener {
            override fun buildData(sheetIndex: Int, privacyFunBean: PrivacyFunBean): List<String> {
                return listOf(
                    privacyFunBean.funAlias.toString(),
                    privacyFunBean.funName.toString(),
                    privacyFunBean.buildStackTrace(),
                    privacyFunBean.count.toString()
                )
            }
        })
        PrivacyLog.i("导出excel成功")
    }
}