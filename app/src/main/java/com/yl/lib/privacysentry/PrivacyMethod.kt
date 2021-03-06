package com.yl.lib.privacysentry

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
import android.database.Cursor
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import android.content.ContentProviderResult

import android.provider.ContactsContract.CommonDataKinds.Email

import android.content.ContentProviderOperation

import android.provider.ContactsContract.CommonDataKinds.Phone

import android.provider.ContactsContract.CommonDataKinds.StructuredName

import android.provider.ContactsContract.RawContacts

import android.content.ContentUris

import android.content.ContentValues

import android.content.ContentResolver
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventCallback
import android.hardware.SensorManager
import android.net.Uri
import android.util.Log
import com.yl.lib.sentry.hook.util.PrivacyLog
import java.lang.StringBuilder


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
            // ???????????????????????????????????????
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
            // ???????????????????????????????????????
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
            // ???????????????????????????????????????
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
            // ???????????????????????????????????????
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
         * ??????imsi
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

        /**
         * ??????sim???????????????
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

            // ??????
            try {
                BluetoothAdapter.getDefaultAdapter().address
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val wifiManager =
                    context.applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
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

            //7.0????????????????????????????????????02:00:00:00:00:00
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
            val localPackageManager = context.packageManager ?: return false
            return localPackageManager.checkPermission(
                permission,
                context.packageName
            ) == PackageManager.PERMISSION_GRANTED
        }


        /**PMS START================================**/
        /**
         * ????????????app?????????????????????
         *
         * @param context ?????????
         * @param pkgName app ??????
         * @return ??????app?????????????????????
         */
        fun isInstalled(@NonNull context: Context, pkgName: String): Boolean {
            if (TextUtils.isEmpty(pkgName)) {
                return false
            }
            // ???????????????????????????????????????
            val packages = getInstalledPackages(context)
            for (i in packages.indices) {
                // ????????????????????????????????????
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
            // ???????????????????????????????????????
            val packageManager = context.packageManager
            return (packageManager.queryIntentActivities(
                Intent(
                    activity,
                    MainActivity::javaClass.javaClass
                ), 0
            )).isNotEmpty()
        }

        fun queryActivityInfo(
            context: Application,
            @NonNull activity: Activity
        ): Boolean {

            // ???????????????????????????????????????
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
         * ??????????????????????????????app??????
         *
         * @param context ?????????
         * @return ???????????????app????????????
         */
        private fun getInstalledPackages(@NonNull context: Context): List<PackageInfo> {
            val packageManager = context.packageManager
            return packageManager.getInstalledPackages(0)
        }
        /**PMS END================================**/

        /**CMS START================================**/
        fun testHookCms(@NonNull context: Context) {
            //?????????????????????
            val cm: ClipboardManager? =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            //?????????????????????
            cm?.setPrimaryClip(ClipData.newPlainText("data", "yl_vd"))
            val cd: ClipData? = cm?.primaryClip

            cm?.text = ("yl_vd123")
            //???????????????????????????
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

        //?????? Android SN(Serial)
        fun getSerial():String{
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                android.os.Build.getSerial()
            } else {
                android.os.Build.SERIAL
            }
        }

        fun  testSensor(context:Context){
            var sensorManager: SensorManager? = null
            var callback: SensorEventCallback? = null
            // ???????????????
            // ???????????????
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            var sensor: Sensor?  = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            // ?????????????????????
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            // ??????
            var lastUpdateTime: Long = 0
            var lastX = 0f
            var lastY = 0f
            var lastZ = 0f
            if (sensor != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    callback = object : SensorEventCallback() {
                        override fun onSensorChanged(event: SensorEvent) {
                            // ??????????????????
                            val currentUpdateTime = System.currentTimeMillis()
                            // ???????????????????????????
                            val timeInterval: Long = currentUpdateTime - lastUpdateTime

                            // ???????????????????????????????????????
                            if (timeInterval < 70) return
                            // ?????????????????????last??????
                            lastUpdateTime = currentUpdateTime

                            // ??????x,y,z??????
                            val x = event.values[0]
                            val y = event.values[1]
                            val z = event.values[2]

                            // ??????x,y,z????????????
                            val deltaX: Float = x - lastX
                            val deltaY: Float = y - lastY
                            val deltaZ: Float = z - lastZ

                            // ????????????????????????last??????
                            lastX = x
                            lastY = y
                            lastZ = z
//                           PrivacyLog.i(" ????????? $deltaX,$deltaY,$deltaZ")
                            val speed =
                                Math.sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble()) / timeInterval * 10000
                            // ?????????????????????????????????
                            if (speed >= 3000) {
                                PrivacyLog.i(" ?????????  ????????")
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
    }
}