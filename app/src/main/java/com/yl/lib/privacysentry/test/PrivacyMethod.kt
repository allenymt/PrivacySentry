package com.yl.lib.privacysentry.test

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventCallback
import android.hardware.SensorManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.SIM_STATE_UNKNOWN
import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.yl.lib.privacysentry.MainActivity
import com.yl.lib.sentry.hook.util.PrivacyLog
import java.io.File
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*


/**
 * @author yulun
 * @sinice 2021-11-16 15:08
 */
class PrivacyMethod {
    object PrivacyMethod {


        /**TMS START================================**/
        /**
         * test for device id
         */
        fun getDeviceId(context: Context?): String {
            if (context == null) {
                return ""
            }
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            return ""
//        }
            var imei = ""
            // 在某些平板上可能会抛出异常
            try {
                val mTelephonyMgr = context
                    .getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
                imei = mTelephonyMgr.getDeviceId()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return imei ?: ""
        }


        fun getDeviceId1(context: Context?): String {
            if (context == null) {
                return ""
            }
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            return ""
//        }
            var imei = ""
            // 在某些平板上可能会抛出异常
            try {
                if (checkPermissions(
                        context,
                        Manifest.permission.READ_PHONE_STATE
                    )
                ) {
                    val mTelephonyMgr = context
                        .getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        imei = mTelephonyMgr.getDeviceId(1)
                    }
                }
            } catch (e: Throwable) {
//                e.printStackTrace()
            }
            return imei ?: ""
        }


        fun getMeid(context: Context?): String {
            if (context == null) {
                return ""
            }
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            return ""
//        }
            var imei = ""
            // 在某些平板上可能会抛出异常
            try {
                val mTelephonyMgr = context
                    .getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    imei = mTelephonyMgr.getMeid()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return imei ?: ""
        }

        /**
         * imei
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun getIMEI(context: Context?): String {
            if (context == null) {
                return ""
            }
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            return ""
//        }
            var imei = ""
            // 在某些平板上可能会抛出异常
            try {
                if (checkPermissions(
                        context,
                        Manifest.permission.READ_PHONE_STATE
                    )
                ) {
                    val mTelephonyMgr = context
                        .getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
                    imei = mTelephonyMgr.imei
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return imei ?: ""
        }

        /**
         * 获得imsi
         * @return
         */
        @SuppressLint("HardwareIds")
        fun getIMSI(context: Context?): String? {
            if (context == null) {
                return ""
            }
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            return ""
//        }
            var imsi = ""
            try {
                if (checkPermissions(
                        context,
                        Manifest.permission.READ_PHONE_STATE
                    )
                ) {
                    val mTelephonyMgr = context
                        .getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
                        ?: return ""
                    imsi =
                        mTelephonyMgr.subscriberId
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return imsi ?: ""
        }

        fun getIp(context: Context) {
            var ip1 = TestInJava.getHostIp()
            PrivacyLog.i("ip1 is $ip1")
            Thread {
                var ip2 = TestInJava.getIpAddress(context)
                PrivacyLog.i(" ip2 is $ip2")
            }.start()
        }


        // 获取sim卡操作码
        fun getSimOperator(context: Context?): String? {
            if (context == null) {
                return ""
            }
            var simOperator = ""
            try {

                val mTelephonyMgr = context
                    .getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
                simOperator =
                    mTelephonyMgr.simOperator
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return simOperator
        }

        fun getSimState(context: Context?): Int {
            if (context == null) {
                return SIM_STATE_UNKNOWN
            }
            var simState  = SIM_STATE_UNKNOWN
//            try {

                val mTelephonyMgr = context
                    .getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
                simState =
                    mTelephonyMgr.simState
//            } catch (e: Throwable) {
//                e.printStackTrace()
//            }
            return simState
        }


        fun getNetworkOperator(context: Context?): String? {
            if (context == null) {
                return ""
            }
            var networkOperator = ""
            try {

                val mTelephonyMgr = context
                    .getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
                networkOperator =
                    mTelephonyMgr.networkOperator
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return networkOperator
        }

        /**
         * 获取sim卡唯一标示
         *
         * @param context
         * @return
         */
        @SuppressLint("HardwareIds")
        fun getICCID(context: Context?): String? {
            if (context == null) {
                return ""
            }
            var iccid = ""
            try {

                val mTelephonyMgr = context
                    .getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
                    ?: return ""
                iccid =
                    mTelephonyMgr.simSerialNumber
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return iccid ?: ""
        }

        /**TMS END================================**/


        val MAC_DEFAULT = "00:00:00:00:00:00"
        val MAC_SYSTEM = "02:00:00:00:00:00"

        /**
         *  wifiInfo.macAddress
         *  networkInterface.hardwareAddress
         *  BluetoothAdapter.address
         */
        fun getMacRaw(context: Context?): String? {
            var mac: String? = MAC_DEFAULT
            if (context == null) {
                return mac
            }

            // 蓝牙
            try {
                BluetoothAdapter.getDefaultAdapter().address
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val wifiManager =
                    context.applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
                wifiManager.isWifiEnabled
                if (wifiManager != null) {
                    val wifiInfo = wifiManager.connectionInfo
                    if (wifiInfo != null) {
                        val result = wifiInfo.macAddress
                        if (result != null && result.length > 0) {
                            mac = result
                            mac = mac.replace("\u0000".toRegex(), "")
                            mac = mac.replace("null".toRegex(), "")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //7.0以上获取不到，获得的都是02:00:00:00:00:00
            getMacV2()
            return if (TextUtils.isEmpty(mac)) MAC_DEFAULT else mac
        }


        fun getMacV2(): String? {
            try {
                val networkInterfaces: Enumeration<*> = NetworkInterface.getNetworkInterfaces()
                while (networkInterfaces.hasMoreElements()) {
                    val networkInterface = networkInterfaces.nextElement() as NetworkInterface
                    val hardwareAddress = networkInterface.hardwareAddress
                    if (hardwareAddress != null && hardwareAddress.size != 0) {
                        val stringBuffer = java.lang.StringBuilder()
                        val var5 = hardwareAddress.size
                        for (var6 in 0 until var5) {
                            val hardwareAddres = hardwareAddress[var6]
                            stringBuffer.append(String.format("%02X:", hardwareAddres))
                        }
                        if (stringBuffer.length > 0) {
                            stringBuffer.deleteCharAt(stringBuffer.length - 1)
                        }
                        return stringBuffer.toString()
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return ""
        }


        private fun checkPermissions(context: Context, permission: String): Boolean {
//            val localPackageManager = context.packageManager ?: return false
//            return localPackageManager.checkPermission(
//                permission,
//                context.packageName
//            ) == PackageManager.PERMISSION_GRANTED
            return true
        }


        /**PMS START================================**/
        /**
         * 判断指定app应用是否已安装
         *
         * @param context 上下文
         * @param pkgName app 包名
         * @return 指定app应用是否已安装
         */
        fun isInstalled(@NonNull context: Context, pkgName: String): Boolean {
            if (TextUtils.isEmpty(pkgName)) {
                return false
            }
            // 获取所有已安装程序的包信息
            val packages = getInstalledPackages(context)
            for (i in packages.indices) {
                // 循环判断是否存在指定包名
                if (packages[i].packageName == pkgName) {
                    return true
                }
            }
            return false
        }

        fun isInstalled2(
            context: Application,
            @NonNull activity: Activity,
            pkgName: String
        ): Boolean {
            if (TextUtils.isEmpty(pkgName)) {
                return false
            }
            // 获取所有已安装程序的包信息
            val packageManager = context.packageManager
            return (packageManager.queryIntentActivities(
                Intent(
                    activity,
                    MainActivity::javaClass.javaClass
                ), 0
            )).isNotEmpty()
        }

        fun isInstalled3(
            context: Application,
            pkgName: String
        ): Boolean {
            if (TextUtils.isEmpty(pkgName)) {
                return false
            }
            // 获取所有已安装程序的包信息
            val packageManager = context.packageManager
            try {
                var info = packageManager.getPackageInfo(
                    pkgName, 0
                )
                return info != null
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }

        fun queryActivityInfo(
            context: Application,
            @NonNull activity: Activity
        ): Boolean {

            // 获取所有已安装程序的包信息
            val packageManager = context.packageManager
            return (packageManager.queryIntentActivityOptions(
                null, null,
                Intent(
                    activity,
                    MainActivity::javaClass.javaClass
                ), 0
            )).isNotEmpty()
        }

        /**
         * 获取手机中所有安装的app应用
         *
         * @param context 上下文
         * @return 所有安装的app应用信息
         */
        private fun getInstalledPackages(@NonNull context: Context): List<PackageInfo> {
            val packageManager = context.packageManager
            return packageManager.getInstalledPackages(0)
        }
        /**PMS END================================**/

        /**CMS START================================**/
        fun testHookCms(@NonNull context: Context) {
            //获取剪切板服务
            val cm: ClipboardManager? =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            cm?.hasPrimaryClip()
            //设置剪切板内容
            cm?.setPrimaryClip(ClipData.newPlainText("data", "yl_vd"))
            val cd: ClipData? = cm?.primaryClip

            cm?.text = ("yl_vd123")
            //获取剪切板数据对象
            cm?.text

            cm?.primaryClipDescription
            val clipStr = cd?.getItemAt(0)?.text.toString()
//            PrivacyLog.i("testHookCms cms data is :$clipStr")
        }

        /**CMS END================================**/

        fun testRunningProcess(@NonNull context: Context) {
            val manager = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningAppProcesses = manager
                .runningAppProcesses
        }

        fun testRunningTask(@NonNull context: Context) {
            val manager = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningAppProcesses = manager
                .getRunningTasks(100)
        }

        fun getAndroidId(context: Context): String? {
            return "" + Settings.Secure.getString(context.contentResolver, "android_id")
        }

        //读取 Android SN(Serial)
        fun getSerial(): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    android.os.Build.getSerial()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return ""
                }
            } else {
                android.os.Build.SERIAL
            }
        }

        fun testSensor(context: Context) {
            var sensorManager: SensorManager? = null
            var callback: SensorEventCallback? = null
            // 摇一摇注册
            // 摇一摇注册
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            var sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            // 获得重力传感器
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            // 注册
            var lastUpdateTime: Long = 0
            var lastX = 0f
            var lastY = 0f
            var lastZ = 0f
            if (sensor != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    callback = object : SensorEventCallback() {
                        override fun onSensorChanged(event: SensorEvent) {
                            // 现在检测时间
                            val currentUpdateTime = System.currentTimeMillis()
                            // 两次检测的时间间隔
                            val timeInterval: Long = currentUpdateTime - lastUpdateTime

                            // 判断是否达到了检测时间间隔
                            if (timeInterval < 70) return
                            // 现在的时间变成last时间
                            lastUpdateTime = currentUpdateTime

                            // 获得x,y,z坐标
                            val x = event.values[0]
                            val y = event.values[1]
                            val z = event.values[2]

                            // 获得x,y,z的变化值
                            val deltaX: Float = x - lastX
                            val deltaY: Float = y - lastY
                            val deltaZ: Float = z - lastZ

                            // 将现在的坐标变成last坐标
                            lastX = x
                            lastY = y
                            lastZ = z
//                           PrivacyLog.i(" 摇了摇 $deltaX,$deltaY,$deltaZ")
                            val speed =
                                Math.sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble()) / timeInterval * 10000
                            // 达到速度阀值，发出提示
                            if (speed >= 3000) {
                                PrivacyLog.i(" 摇了摇  😂😂")
                                sensorManager.unregisterListener(this)
                            }
                        }
                    }
                    sensorManager.registerListener(
                        callback,
                        sensor,
                        SensorManager.SENSOR_DELAY_GAME
                    )
                }
            }
        }

        fun testGetSensorList(context: Context) {
            var sensorManager: SensorManager? =
                context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            // 获取传感器列表
            var sensor: List<Sensor>? = sensorManager?.getSensorList(Sensor.TYPE_ACCELEROMETER)
            PrivacyLog.i("sensor size is :${sensor?.size}")
        }

        /**
         * 返回SD卡根路径
         *
         * @return
         */
        fun getSdcardRoot(context: Context): String? {
            var path: String? = null
            if (isSdcardReady()
                && hasExternalStoragePermission(context)
            ) {
                val sdDir = Environment.getExternalStorageDirectory()
                path = sdDir.absolutePath
            }
            var newPath = getPath(context)
            return path
        }

        fun isSdcardReady(): Boolean {
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        }

        fun getPath(context: Context): String? {
            var dir: File? = null
            val state = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
            dir = if (state) {
                if (Build.VERSION.SDK_INT >= 29) {
                    //Android10之后
                    context.getExternalFilesDir(null)
                } else {
                    Environment.getExternalStorageDirectory()
                }
            } else {
                Environment.getRootDirectory()
            }
            return dir.toString()
        }

        /**
         * 是否有写扩展存储的权限
         *
         * @param context
         * @return
         */
        fun hasExternalStoragePermission(context: Context): Boolean {
            val perm =
                context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE")
            return perm == PackageManager.PERMISSION_GRANTED
        }

        fun getScanResults(context: Context){
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager?
            val scanResults: List<ScanResult> = wifiManager!!.scanResults
            for (scanResult in scanResults) {
                val ssid: String = scanResult.SSID // Wi-Fi 名称
                val bssid: String = scanResult.BSSID // Wi-Fi MAC 地址
                val level: Int = scanResult.level // 信号强度
                val frequency: Int = scanResult.frequency // 频率
                val capabilities: String = scanResult.capabilities // 加密类型
                // 处理扫描结果
                PrivacyLog.i("getScanResults ssid is :$ssid,bssid is :$bssid,level is :$level,frequency is :$frequency,capabilities is :$capabilities")
            }
        }
    }
}