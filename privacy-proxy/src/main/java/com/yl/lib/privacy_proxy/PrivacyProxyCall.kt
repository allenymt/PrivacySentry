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
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import com.yl.lib.privacy_annotation.PrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyMethodProxy
import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.util.PrivacyLog
import com.yl.lib.sentry.hook.util.PrivacyUtil
import java.net.NetworkInterface

/**
 * @author yulun
 * @sinice 2021-12-22 14:23
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
            originalClass = Settings.Secure::class,
            originalMethod = "getString",
            originalOpcode = MethodInvokeOpcode.INVOKESTATIC
        )
        @JvmStatic
        fun getString(contentResolver: ContentResolver?, type: String?): String? {
            var result = ""
            try {
                doFilePrinter("getString", "读取系统信息", args = type)
                if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                    return ""
                }
                result = Settings.Secure.getString(
                    contentResolver,
                    type
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
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
            doFilePrinter("getDeviceId", "获取设备id-getDeviceId()")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.getDeviceId()
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
            doFilePrinter("getDeviceId", "获取设备id-getDeviceId(I)")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.getDeviceId(index)
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
            doFilePrinter("getSubscriberId", "获取设备id-getSubscriberId(I)")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
            }
            return manager.subscriberId
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getSubscriberId",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getSubscriberId(manager: TelephonyManager, index: Int): String? {
            doFilePrinter("getSubscriberId", "获取设备id-getSubscriberId()")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
            }
            return manager.subscriberId
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getImei",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getImei(manager: TelephonyManager): String? {
            doFilePrinter("getImei", "获取设备id-getImei()")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.getImei()
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
            doFilePrinter("getImei", "获取设备id-getImei()")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.getImei(index)
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
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
            }
            doFilePrinter("getSimSerialNumber", "获取设备id-getSimSerialNumber()")
            return manager.getSimSerialNumber()
        }

        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getSimSerialNumber",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getSimSerialNumber(manager: TelephonyManager, index: Int): String? {
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
            }
            doFilePrinter("getSimSerialNumber", "获取设备id-getSimSerialNumber()")
            return manager.getSimSerialNumber()
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
            doFilePrinter("getMacAddress", "获取mac地址-getMacAddress")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
            }
            return manager.getMacAddress()
        }

        /**
         * 读取WIFI的SSID
         */
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = WifiInfo::class,
            originalMethod = "getSSID",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getSSID(manager: WifiInfo): String? {
            doFilePrinter("getSSID", "802.11网络的服务集标识符")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
            }
            return manager.ssid
        }

        /**
         * 读取WIFI的SSID
         */
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = WifiInfo::class,
            originalMethod = "getBSSID",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getBSSID(manager: WifiInfo): String? {
            doFilePrinter("getBSSID", "802.11网络的服务集标识符")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
            }
            return manager.bssid
        }

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
            doFilePrinter("getHardwareAddress", "获取mac地址-getHardwareAddress")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ByteArray(1)
            }
            return manager.hardwareAddress
        }

        @PrivacyMethodProxy(
            originalClass = BluetoothAdapter::class,
            originalMethod = "getAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getAddress(manager: BluetoothAdapter): String? {
            doFilePrinter("getAddress", "获取蓝牙地址-getAddress")
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                return ""
            }
            return manager.address ?: ""
        }

        private fun doFilePrinter(
            funName: String,
            methodDocumentDesc: String = "",
            args: String? = ""
        ) {
            if (PrivacySentry.Privacy.getBuilder()?.isEnableFileResult() == false) {
                PrivacyLog.e("disable print file: funName is $funName methodDocumentDesc is $methodDocumentDesc")
                return
            }
            PrivacySentry.Privacy.getBuilder()?.getPrinterList()?.forEach {
                it.filePrint(
                    funName,
                    methodDocumentDesc + if (args?.isNotEmpty() == true) "--参数: $args" else "",
                    PrivacyUtil.Util.getStackTrace()
                )
            }
        }
    }
}