package com.yl.lib.privacy_proxy

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.DhcpInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.CellInfo
import android.telephony.TelephonyManager
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import com.yl.lib.privacy_annotation.PrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyMethodProxy
import com.yl.lib.privacy_proxy.PrivacyProxyUtil.Util.doFilePrinter
import com.yl.lib.sentry.hook.PrivacySentry
import java.net.NetworkInterface

/**
 * @author yulun
 * @since 2021-12-22 14:23
 * 大部分敏感api拦截代理
 */
@Keep
open class PrivacyProxyCall {

    // kotlin里实际解析的是这个PrivacyProxyCall$Proxy 内部类
    @PrivacyClassProxy
    @Keep
    object Proxy {

        // 这个方法的注册放在了PrivacyProxyCall2中，提供了一个java注册的例子
        @PrivacyMethodProxy(
            originalClass = ActivityManager::class,
            originalMethod = "getRunningTasks",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getRunningTasks(
            manager: ActivityManager,
            maxNum: Int
        ): List<ActivityManager.RunningTaskInfo?>? {
            doFilePrinter("getRunningTasks", methodDocumentDesc = "获取当前运行中的任务")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return emptyList()
            }
            return manager.getRunningTasks(maxNum)
        }

        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = ActivityManager::class,
            originalMethod = "getRecentTasks",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getRecentTasks(
            manager: ActivityManager,
            maxNum: Int,
            flags: Int
        ): List<ActivityManager.RecentTaskInfo>? {
            doFilePrinter("getRecentTasks", methodDocumentDesc = "获取最近运行中的任务")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return emptyList()
            }
            return manager.getRecentTasks(maxNum, flags)
        }


        @PrivacyMethodProxy(
            originalClass = ActivityManager::class,
            originalMethod = "getRunningAppProcesses",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getRunningAppProcesses(manager: ActivityManager): List<ActivityManager.RunningAppProcessInfo> {
            doFilePrinter("getRunningAppProcesses", methodDocumentDesc = "获取当前运行中的进程")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return emptyList()
            }
            return manager.getRunningAppProcesses()
        }

        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "getInstalledPackages",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getInstalledPackages(manager: PackageManager, flags: Int): List<PackageInfo> {
            doFilePrinter("getInstalledPackages", methodDocumentDesc = "获取安装包-getInstalledPackages")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return emptyList()
            }
            return manager.getInstalledPackages(flags)
        }

        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "queryIntentActivities",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun queryIntentActivities(
            manager: PackageManager,
            intent: Intent,
            flags: Int
        ): List<ResolveInfo> {
            doFilePrinter(
                "queryIntentActivities",
                methodDocumentDesc = "读安装列表-queryIntentActivities"
            )
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return emptyList()
            }
            return manager.queryIntentActivities(intent, flags)
        }

        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "queryIntentActivityOptions",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun queryIntentActivityOptions(
            manager: PackageManager,
            caller: ComponentName?,
            specifics: Array<Intent?>?,
            intent: Intent,
            flags: Int
        ): List<ResolveInfo> {
            doFilePrinter(
                "queryIntentActivityOptions",
                methodDocumentDesc = "读安装列表-queryIntentActivityOptions"
            )
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return emptyList()
            }
            return manager.queryIntentActivityOptions(caller, specifics, intent, flags)
        }


        /**
         * 读取基站信息，需要开启定位
         */
        @JvmStatic
        @SuppressLint("MissingPermission")
        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getAllCellInfo",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getAllCellInfo(manager: TelephonyManager): List<CellInfo>? {
            doFilePrinter("getAllCellInfo", methodDocumentDesc = "读取基站信息，需要开启定位")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return emptyList()
            }
            return manager.getAllCellInfo()
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getMeid",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getMeid(manager: TelephonyManager): String? {
            doFilePrinter("getMeid", methodDocumentDesc = "移动设备标识符-getMeid()")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.getMeid()
            } else {
                ""
            }
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getMeid",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getMeid(manager: TelephonyManager, index: Int): String? {
            doFilePrinter("getMeid", "移动设备标识符-getMeid(I)")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.getMeid(index)
            } else {
                ""
            }
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getDeviceId",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getDeviceId(manager: TelephonyManager): String? {
            var key = "TelephonyManager-getDeviceId"
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                doFilePrinter(key, "获取设备id-getDeviceId()", bVisitorModel = true)
                return ""
            }
            if (PrivacyProxyUtil.Util.hasReadStaticParam(key)) {
                doFilePrinter(key, "获取设备id-getDeviceId()", bCache = true)
                return PrivacyProxyUtil.Util.getCacheStaticParam("", key)
            }

            doFilePrinter(key, "获取设备id-getDeviceId()")
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var value = ""
                try {
                    value = manager.getDeviceId()
                } catch (e: Throwable) {
                    //不管有没有申请，部分机子直接跑异常
                    throw e
                } finally {
                    PrivacyProxyUtil.Util.putCacheStaticParam(value ?: "", key)
                }
                return value
            } else {
                ""
            }
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getDeviceId",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getDeviceId(manager: TelephonyManager, index: Int): String? {
            var key = "TelephonyManager-getDeviceId-$index"

            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                doFilePrinter(key, "获取设备id-getDeviceId(I)", bVisitorModel = true)
                return ""
            }
            if (PrivacyProxyUtil.Util.hasReadStaticParam(key)) {
                doFilePrinter(key, "获取设备id-getDeviceId(I)", bCache = true)
                return PrivacyProxyUtil.Util.getCacheStaticParam("", key)
            }
            doFilePrinter(key, "获取设备id-getDeviceId(I)")
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var value = ""
                try {
                    value = manager.getDeviceId(index)
                } catch (e: Throwable) {
                    //不管有没有申请，部分机子直接跑异常
                    throw e
                } finally {
                    PrivacyProxyUtil.Util.putCacheStaticParam(value ?: "", key)
                }
                return value
            } else {
                ""
            }
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getSubscriberId",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getSubscriberId(manager: TelephonyManager): String? {
            var key = "TelephonyManager-getSubscriberId"

            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                doFilePrinter(key, "获取设备id-getSubscriberId(I)", bVisitorModel = true)
                return ""
            }
            if (PrivacyProxyUtil.Util.hasReadStaticParam(key)) {
                doFilePrinter(key, "获取设备id-getSubscriberId(I)", bCache = true)
                return PrivacyProxyUtil.Util.getCacheStaticParam("", key)
            }

            doFilePrinter(key, "获取设备id-getSubscriberId(I)")
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var value = ""
                try {
                    value = manager.subscriberId
                } catch (e: Throwable) {
                    //不管有没有申请，部分机子直接跑异常
                    throw e
                } finally {
                    PrivacyProxyUtil.Util.putCacheStaticParam(value ?: "", key)
                }
                return value
            } else {
                ""
            }
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getSubscriberId",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getSubscriberId(manager: TelephonyManager, index: Int): String? {
            return getSubscriberId(manager)
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getImei",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getImei(manager: TelephonyManager): String? {
            var key = "TelephonyManager-getImei"

            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                doFilePrinter(key, "获取设备id-getImei()", bVisitorModel = true)
                return ""
            }
            if (PrivacyProxyUtil.Util.hasReadStaticParam(key)) {
                doFilePrinter(key, "获取设备id-getImei()", bCache = true)
                return PrivacyProxyUtil.Util.getCacheStaticParam("", key)
            }

            doFilePrinter(key, "获取设备id-getImei()")
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var value = ""
                try {
                    value = manager.getImei()
                } catch (e: Throwable) {
                    //不管有没有申请，部分机子直接跑异常
                    throw e
                } finally {
                    PrivacyProxyUtil.Util.putCacheStaticParam(value ?: "", key)
                }
                return value
            } else {
                ""
            }
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getImei",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getImei(manager: TelephonyManager, index: Int): String? {
            var key = "TelephonyManager-getImei-$index"
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                doFilePrinter(key, "获取设备id-getImei(I)", bVisitorModel = true)
                return ""
            }
            if (PrivacyProxyUtil.Util.hasReadStaticParam(key)) {
                doFilePrinter(key, "获取设备id-getImei(I)", bCache = true)
                return PrivacyProxyUtil.Util.getCacheStaticParam("", key)
            }

            doFilePrinter(key, "获取设备id-getImei(I)")
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var value = ""
                try {
                    value = manager.getImei(index)
                } catch (e: Throwable) {
                    //不管有没有申请，部分机子直接跑异常
                    throw e
                } finally {
                    PrivacyProxyUtil.Util.putCacheStaticParam(value ?: "", key)
                }
                return value
            } else {
                ""
            }
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getSimSerialNumber",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getSimSerialNumber(manager: TelephonyManager): String? {
            var key = "TelephonyManager-getSimSerialNumber"
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                doFilePrinter(key, "获取设备id-getSimSerialNumber()", bVisitorModel = true)
                return ""
            }
            if (PrivacyProxyUtil.Util.hasReadStaticParam(key)) {
                doFilePrinter(key, "获取设备id-getSimSerialNumber()", bCache = true)
                return PrivacyProxyUtil.Util.getCacheStaticParam("", key)
            }
            doFilePrinter(key, "获取设备id-getSimSerialNumber()")
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var value = ""
                try {
                    value = manager.getSimSerialNumber()
                } catch (e: Throwable) {
                    //不管有没有申请，部分机子直接跑异常
                    throw e
                } finally {
                    PrivacyProxyUtil.Util.putCacheStaticParam(value ?: "", key)
                }
                return value
            } else {
                ""
            }
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getSimSerialNumber",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getSimSerialNumber(manager: TelephonyManager, index: Int): String? {
            return getSimSerialNumber(manager)
        }


        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getLine1Number",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @SuppressLint("MissingPermission")
        @JvmStatic
        fun getLine1Number(manager: TelephonyManager): String? {

            var key = "TelephonyManager-getLine1Number"

            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                doFilePrinter(key, "获取手机号-getLine1Number", bVisitorModel = true)
                return ""
            }
            if (PrivacyProxyUtil.Util.hasReadStaticParam(key)) {
                doFilePrinter(key, "获取手机号-getLine1Number", bCache = true)
                return PrivacyProxyUtil.Util.getCacheStaticParam("", key)
            }
            doFilePrinter(key, "获取手机号-getLine1Number")
            var value = ""
            try {
                value = manager.line1Number
            } catch (e: Throwable) {
                //不管有没有申请，部分机子直接跑异常
                throw e
            } finally {
                PrivacyProxyUtil.Util.putCacheStaticParam(value ?: "", key)
            }
            return value
        }

        @PrivacyMethodProxy(
            originalClass = ClipboardManager::class,
            originalMethod = "getPrimaryClip",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getPrimaryClip(manager: ClipboardManager): ClipData? {
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ClipData.newPlainText("Label", "")
            }
            doFilePrinter("getPrimaryClip", "获取剪贴板内容-getPrimaryClip")
            return manager.primaryClip
        }

        @PrivacyMethodProxy(
            originalClass = ClipboardManager::class,
            originalMethod = "getPrimaryClipDescription",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getPrimaryClipDescription(manager: ClipboardManager): ClipDescription? {
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ClipDescription("", arrayOf(MIMETYPE_TEXT_PLAIN))
            }
            doFilePrinter("getPrimaryClipDescription", "获取剪贴板内容-getPrimaryClipDescription")
            return manager.primaryClipDescription
        }

        @PrivacyMethodProxy(
            originalClass = ClipboardManager::class,
            originalMethod = "getText",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getText(manager: ClipboardManager): CharSequence? {
            doFilePrinter("getText", "获取剪贴板内容-getText")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
            }
            return manager.text
        }

        @PrivacyMethodProxy(
            originalClass = ClipboardManager::class,
            originalMethod = "setPrimaryClip",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun setPrimaryClip(manager: ClipboardManager, clip: ClipData) {
            doFilePrinter("setPrimaryClip", "设置剪贴板内容-setPrimaryClip")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return
            }
            manager.setPrimaryClip(clip)
        }

        @PrivacyMethodProxy(
            originalClass = ClipboardManager::class,
            originalMethod = "setText",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun setText(manager: ClipboardManager, clip: CharSequence) {
            doFilePrinter("setText", "设置剪贴板内容-setText")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return
            }
            manager.text = clip
        }

        @PrivacyMethodProxy(
            originalClass = WifiInfo::class,
            originalMethod = "getMacAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getMacAddress(manager: WifiInfo): String? {
            var key = "WifiInfo-getMacAddress"

            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                doFilePrinter(key, "获取mac地址-getMacAddress", bVisitorModel = true)
                return ""
            }
            if (PrivacyProxyUtil.Util.hasReadStaticParam(key)) {
                doFilePrinter(key, "获取mac地址-getMacAddress", bCache = true)
                return PrivacyProxyUtil.Util.getCacheStaticParam("", key)
            }
            doFilePrinter(key, "获取mac地址-getMacAddress")
            var value = ""
            try {
                value = manager.getMacAddress()
            } catch (e: Throwable) {
                //不管有没有申请，部分机子直接跑异常
                throw e
            } finally {
                PrivacyProxyUtil.Util.putCacheStaticParam(value ?: "", key)
            }
            return value
        }

//        /**
//         * 读取WIFI的SSID
//         */
//        @JvmStatic
//        @PrivacyMethodProxy(
//            originalClass = WifiInfo::class,
//            originalMethod = "getSSID",
//            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
//        )
//        fun getSSID(manager: WifiInfo): String? {
//            doFilePrinter("getSSID", "802.11网络的服务集标识符")
//            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
//                return ""
//            }
//            return manager.ssid
//        }

//        /**
//         * 读取WIFI的SSID
//         */
//        @JvmStatic
//        @PrivacyMethodProxy(
//            originalClass = WifiInfo::class,
//            originalMethod = "getBSSID",
//            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
//        )
//        fun getBSSID(manager: WifiInfo): String? {
//            doFilePrinter("getBSSID", "802.11网络的服务集标识符")
//            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
//                return ""
//            }
//            return manager.bssid
//        }

        /**
         * 读取WIFI扫描结果
         */
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = WifiManager::class,
            originalMethod = "getScanResults",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getScanResults(manager: WifiManager): List<ScanResult>? {
            doFilePrinter("getScanResults", "读取WIFI扫描结果")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return emptyList()
            }
            return manager.getScanResults()
        }


        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = SensorManager::class,
            originalMethod = "getSensorList",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getSensorList(manager: SensorManager, type: Int): List<Sensor>? {
            doFilePrinter("getSensorList", "获取可用传感器")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return emptyList()
            }
            return manager.getSensorList(type)
        }


        /**
         * 读取DHCP信息
         */
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = WifiManager::class,
            originalMethod = "getDhcpInfo",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getDhcpInfo(manager: WifiManager): DhcpInfo? {
            doFilePrinter("getDhcpInfo", "DHCP地址")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return null
            }
            return manager.getDhcpInfo()
        }

        /**
         * 读取DHCP信息
         */
        @SuppressLint("MissingPermission")
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = WifiManager::class,
            originalMethod = "getConfiguredNetworks",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getConfiguredNetworks(manager: WifiManager): List<WifiConfiguration>? {
            doFilePrinter("getConfiguredNetworks", "前台用户配置的所有网络的列表")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return emptyList()
            }
            return manager.getConfiguredNetworks()
        }


        /**
         * 读取位置信息
         */
        @JvmStatic
        @SuppressLint("MissingPermission")
        @PrivacyMethodProxy(
            originalClass = LocationManager::class,
            originalMethod = "getLastKnownLocation",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getLastKnownLocation(
            manager: LocationManager, provider: String
        ): Location? {
            doFilePrinter("getLastKnownLocation", "读取上一次获取的位置信息")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                // 这里直接写空可能有风险
                return null
            }
            return manager.getLastKnownLocation(provider)
        }


        @SuppressLint("MissingPermission")
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = LocationManager::class,
            originalMethod = "requestLocationUpdates",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun requestLocationUpdates(
            manager: LocationManager, provider: String, minTime: Long, minDistance: Float,
            listener: LocationListener
        ) {
            doFilePrinter("requestLocationUpdates", "监视精细行动轨迹")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return
            }
            manager.requestLocationUpdates(provider, minTime, minDistance, listener)
        }

        @PrivacyMethodProxy(
            originalClass = NetworkInterface::class,
            originalMethod = "getHardwareAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getHardwareAddress(manager: NetworkInterface): ByteArray? {
            var key = "NetworkInterface-getHardwareAddress"

            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                doFilePrinter(key, "获取mac地址-getHardwareAddress", bVisitorModel = true)
                return ByteArray(1)
            }
            if (PrivacyProxyUtil.Util.hasReadStaticParam(key)) {
                doFilePrinter(key, "获取mac地址-getHardwareAddress", bCache = true)
                return PrivacyProxyUtil.Util.getCacheStaticParam(ByteArray(1), key)
            }

            doFilePrinter(key, "获取mac地址-getHardwareAddress")
            var value = ByteArray(1)
            try {
                value = manager.hardwareAddress
            } catch (e: Throwable) {
                //不管有没有申请，部分机子直接跑异常
                throw e
            } finally {
                PrivacyProxyUtil.Util.putCacheStaticParam(value ?: "", key)
            }
            return value
        }

        @PrivacyMethodProxy(
            originalClass = BluetoothAdapter::class,
            originalMethod = "getAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getAddress(manager: BluetoothAdapter): String? {
            var key = "BluetoothAdapter-getAddress"

            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                doFilePrinter(key, "获取蓝牙地址-getAddress", bVisitorModel = true)
                return ""
            }
            if (PrivacyProxyUtil.Util.hasReadStaticParam(key)) {
                doFilePrinter(key, "获取蓝牙地址-getAddress", bCache = true)
                return PrivacyProxyUtil.Util.getCacheStaticParam("", key)
            }

            doFilePrinter(key, "获取蓝牙地址-getAddress")
            var value = ""
            try {
                value = manager.address
            } catch (e: Throwable) {
                //不管有没有申请，部分机子直接跑异常
                throw e
            } finally {
                PrivacyProxyUtil.Util.putCacheStaticParam(value ?: "", key)
            }
            return value
        }


        @PrivacyMethodProxy(
            originalClass = Settings.Secure::class,
            originalMethod = "getString",
            originalOpcode = MethodInvokeOpcode.INVOKESTATIC
        )
        @JvmStatic
        fun getString(contentResolver: ContentResolver?, type: String?): String? {
            var key = "Secure-getString-$type"
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                doFilePrinter("getString", "读取系统信息", args = type, bVisitorModel = true)
                return ""
            }

            if (PrivacyProxyUtil.Util.hasReadStaticParam(key)) {
                doFilePrinter("getString", "读取系统信息", args = type, bCache = true)
                return PrivacyProxyUtil.Util.getCacheStaticParam("", key)
            }

            doFilePrinter("getString", "读取系统信息", args = type)
            var result = ""
            try {
                result = Settings.Secure.getString(
                    contentResolver,
                    type
                )
            } catch (e: Throwable) {
                //不管有没有申请，部分机子直接跑异常
                throw e
            } finally {
                PrivacyProxyUtil.Util.putCacheStaticParam(result ?: "", "Secure-getString-$type")
            }
            return result
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @PrivacyMethodProxy(
            originalClass = android.os.Build::class,
            originalMethod = "getSerial",
            originalOpcode = MethodInvokeOpcode.INVOKESTATIC
        )
        @JvmStatic
        fun getSerial(): String? {
            var result = ""
            var key = "getSerial"
            try {
                if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                    doFilePrinter("getSerial", "读取Serial", bVisitorModel = true)
                    return ""
                }

                if (PrivacyProxyUtil.Util.hasReadStaticParam(key)) {
                    doFilePrinter("getSerial", "读取Serial", bCache = true)
                    return PrivacyProxyUtil.Util.getCacheStaticParam("", key)
                }

                doFilePrinter("getSerial", "读取Serial")
                result = Build.getSerial()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                PrivacyProxyUtil.Util.putCacheStaticParam(result ?: "", key)
            }
            return result
        }

    }
}