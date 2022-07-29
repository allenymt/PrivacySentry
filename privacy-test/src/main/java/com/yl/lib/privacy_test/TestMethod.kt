package com.yl.lib.privacy_test

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import java.net.NetworkInterface


/**
 * @author yulun
 * @sinice 2021-11-16 15:08
 */
class TestMethod {
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
                if (checkPermissions(
                        context,
                        Manifest.permission.READ_PHONE_STATE
                    )
                ) {
                    val mTelephonyMgr = context
                        .getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
                    imei = mTelephonyMgr.getDeviceId()
                }
            } catch (e: Throwable) {
//                e.printStackTrace()
            }
            return imei ?: ""
        }


        @RequiresApi(Build.VERSION_CODES.M)
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
                        .getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
                    imei = mTelephonyMgr.getDeviceId(1)
                }
            } catch (e: Throwable) {
//                e.printStackTrace()
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
                        .getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
                    imei = mTelephonyMgr.getImei()
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
                        .getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
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
                if (checkPermissions(
                        context,
                        Manifest.permission.READ_PHONE_STATE
                    )
                ) {
                    val mTelephonyMgr = context
                        .getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
                        ?: return ""
                    iccid =
                        mTelephonyMgr.simSerialNumber
                }
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
         */
        fun getMacRaw(context: Context?): String? {
            var mac: String? = MAC_DEFAULT
            if (context == null) {
                return mac
            }
            try {
                val wifiManager =
                    context.applicationContext.getSystemService(Activity.WIFI_SERVICE) as WifiManager
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
            if (mac == null || TextUtils.equals(mac, MAC_DEFAULT) || TextUtils.equals(
                    mac, MAC_SYSTEM
                )
            ) {
                try {
                    var networkInterface = NetworkInterface.getByName("eth1")
                    if (networkInterface == null) {
                        networkInterface = NetworkInterface.getByName("wlan0")
                    }
                    if (networkInterface == null) {
                        return mac
                    }
                    val address = networkInterface.hardwareAddress ?: return mac
                    val builder = StringBuilder()
                    for (b in address) {
                        builder.append(String.format("%02X:", b))
                    }
                    if (builder.length > 0) {
                        builder.deleteCharAt(builder.length - 1)
                    }
                    mac = builder.toString()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
            return if (TextUtils.isEmpty(mac)) MAC_DEFAULT else mac
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
            //设置剪切板内容
            cm?.setPrimaryClip(ClipData.newPlainText("data", "yl_vd"))
            //获取剪切板数据对象
            val cd: ClipData? = cm?.primaryClip
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
    }
}