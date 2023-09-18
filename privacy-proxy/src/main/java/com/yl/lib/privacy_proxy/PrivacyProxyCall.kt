package com.yl.lib.privacy_proxy

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.pm.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.DhcpInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.telephony.CellInfo
import android.telephony.TelephonyManager
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import com.yl.lib.privacy_annotation.PrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyMethodProxy
import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.cache.CachePrivacyManager
import com.yl.lib.sentry.hook.cache.CacheUtils
import com.yl.lib.sentry.hook.util.PrivacyClipBoardManager
import com.yl.lib.sentry.hook.util.PrivacyLog
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil.Util.doFilePrinter
import com.yl.lib.sentry.hook.util.PrivacyUtil
import java.io.File
import java.net.Inet4Address
import java.net.InetAddress
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
            doFilePrinter("getRunningTasks", methodDocumentDesc = "当前运行中的任务")
            if (PrivacySentry.Privacy.inDangerousState()) {
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
            doFilePrinter("getRecentTasks", methodDocumentDesc = "最近运行中的任务")
            if (PrivacySentry.Privacy.inDangerousState()) {
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
            doFilePrinter("getRunningAppProcesses", methodDocumentDesc = "当前运行中的进程")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return emptyList()
            }

            var appProcess: List<ActivityManager.RunningAppProcessInfo> = emptyList()
            try {
                // 线上三星11和12的机子 有上报，量不大
                appProcess = manager.runningAppProcesses
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return appProcess
        }

        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "getInstalledPackages",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getInstalledPackages(manager: PackageManager, flags: Int): List<PackageInfo> {
            doFilePrinter("getInstalledPackages", methodDocumentDesc = "安装包-getInstalledPackages")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return emptyList()
            }
            return manager.getInstalledPackages(flags)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "getPackageInfo",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getPackageInfo(
            manager: PackageManager, versionedPackage: VersionedPackage,
            flags: Int
        ): PackageInfo? {

//            if (PrivacySentry.Privacy.inDangerousState()) {
//                doFilePrinter(
//                    "getPackageInfo",
//                    methodDocumentDesc = "安装包-getPackageInfo-${versionedPackage.packageName}",
//                    bVisitorModel = true
//                )
//                throw PackageManager.NameNotFoundException("getPackageInfo-${versionedPackage.packageName}")
//            }
            doFilePrinter(
                "getPackageInfo",
                methodDocumentDesc = "安装包-getPackageInfo-${versionedPackage.packageName}"
            )
            return manager.getPackageInfo(versionedPackage, flags)
        }

        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "getPackageInfo",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getPackageInfo(
            manager: PackageManager,
            packageName: String,
            flags: Int
        ): PackageInfo? {
//            if (PrivacySentry.Privacy.inDangerousState()) {
//                doFilePrinter(
//                    "getPackageInfo",
//                    methodDocumentDesc = "安装包-getPackageInfo-${packageName}",
//                    bVisitorModel = true
//                )
//                throw PackageManager.NameNotFoundException("getPackageInfo-${packageName}")
//            }
            doFilePrinter(
                "getPackageInfo",
                methodDocumentDesc = "安装包-getPackageInfo-${packageName}"
            )
            return manager.getPackageInfo(packageName, flags)
        }

        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "getInstalledPackagesAsUser",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getInstalledPackagesAsUser(
            manager: PackageManager,
            flags: Int,
            userId: Int
        ): List<PackageInfo> {
            doFilePrinter(
                "getInstalledPackagesAsUser",
                methodDocumentDesc = "安装包-getInstalledPackagesAsUser"
            )
//            if (PrivacySentry.Privacy.inDangerousState()) {
//                return emptyList()
//            }
            return getInstalledPackages(manager, flags);
        }

        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "getInstalledApplications",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getInstalledApplications(manager: PackageManager, flags: Int): List<ApplicationInfo> {
            doFilePrinter(
                "getInstalledApplications",
                methodDocumentDesc = "安装包-getInstalledApplications"
            )
//            if (PrivacySentry.Privacy.inDangerousState()) {
//                return emptyList()
//            }
            return manager.getInstalledApplications(flags)
        }

        @PrivacyMethodProxy(
            originalClass = PackageManager::class,
            originalMethod = "getInstalledApplicationsAsUser",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getInstalledApplicationsAsUser(
            manager: PackageManager, flags: Int,
            userId: Int
        ): List<ApplicationInfo> {
            doFilePrinter(
                "getInstalledApplicationsAsUser",
                methodDocumentDesc = "安装包-getInstalledApplicationsAsUser"
            )
//            if (PrivacySentry.Privacy.inDangerousState()) {
//                return emptyList()
//            }
            return getInstalledApplications(manager, flags);
        }


        // 这个方法比较特殊，是否合规完全取决于intent参数
        // 如果指定了自己的包名，那可以认为是合规的，因为是查自己APP的AC
        // 如果没有指定包名，那就是查询了其他APP的Ac，这不合规
        // 思考，直接在SDK里拦截肯定不合适，对于业务方来说太黑盒了，如果触发bug开发会崩溃的，所以我们只打日志为业务方提供信息
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
            var paramBuilder = StringBuilder()
            var legal = true
            intent?.also {
                intent?.categories?.also {
                    paramBuilder.append("-categories:").append(it.toString()).append("\n")
                }
                intent?.`package`?.also {
                    paramBuilder.append("-packageName:").append(it).append("\n")
                }
                intent?.data?.also {
                    paramBuilder.append("-data:").append(it.toString()).append("\n")
                }
                intent?.component?.packageName?.also {
                    paramBuilder.append("-packageName:").append(it).append("\n")
                }
            }

            if (paramBuilder.isEmpty()) {
                legal = false
            }

            //不指定包名，我们认为这个查询不合法
            if (!paramBuilder.contains("packageName")) {
                legal = false
            }
            paramBuilder.append("-合法查询:${legal}").append("\n")
            doFilePrinter(
                "queryIntentActivities",
                methodDocumentDesc = "读安装列表-queryIntentActivities${paramBuilder?.toString()}"
            )
            if (PrivacySentry.Privacy.inDangerousState()) {
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
            if (PrivacySentry.Privacy.inDangerousState()) {
                return emptyList()
            }
            return manager.queryIntentActivityOptions(caller, specifics, intent, flags)
        }


        /**
         * 基站信息，需要开启定位
         */
        @JvmStatic
        @SuppressLint("MissingPermission")
        @PrivacyMethodProxy(
            originalClass = TelephonyManager::class,
            originalMethod = "getAllCellInfo",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getAllCellInfo(manager: TelephonyManager): List<CellInfo>? {
            doFilePrinter("getAllCellInfo", methodDocumentDesc = "定位-基站信息")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return emptyList()
            }
            return manager.getAllCellInfo()
        }

        @PrivacyMethodProxy(
            originalClass = ClipboardManager::class,
            originalMethod = "getPrimaryClip",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getPrimaryClip(manager: ClipboardManager): ClipData? {
            if (PrivacySentry.Privacy.inDangerousState()) {
                return ClipData.newPlainText("Label", "")
            }
            if (!PrivacyClipBoardManager.isReadClipboardEnable()) {
                doFilePrinter("getPrimaryClip", "读取系统剪贴板关闭")
                return ClipData.newPlainText("Label", "")
            }

            doFilePrinter("getPrimaryClip", "剪贴板内容-getPrimaryClip")
            return manager.primaryClip
        }

        @PrivacyMethodProxy(
            originalClass = ClipboardManager::class,
            originalMethod = "getPrimaryClipDescription",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getPrimaryClipDescription(manager: ClipboardManager): ClipDescription? {
            if (PrivacySentry.Privacy.inDangerousState()) {
                return ClipDescription("", arrayOf(MIMETYPE_TEXT_PLAIN))
            }

            if (!PrivacyClipBoardManager.isReadClipboardEnable()) {
                doFilePrinter("getPrimaryClipDescription", "读取系统剪贴板关闭")
                return ClipDescription("", arrayOf(MIMETYPE_TEXT_PLAIN))
            }

            doFilePrinter("getPrimaryClipDescription", "剪贴板内容-getPrimaryClipDescription")
            return manager.primaryClipDescription
        }

        @PrivacyMethodProxy(
            originalClass = ClipboardManager::class,
            originalMethod = "getText",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getText(manager: ClipboardManager): CharSequence? {

            if (PrivacySentry.Privacy.inDangerousState()) {
                return ""
            }

            if (!PrivacyClipBoardManager.isReadClipboardEnable()) {
                doFilePrinter("getText", "读取系统剪贴板关闭")
                return ""
            }
            doFilePrinter("getText", "剪贴板内容-getText")
            return manager.text
        }

        @PrivacyMethodProxy(
            originalClass = ClipboardManager::class,
            originalMethod = "setPrimaryClip",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun setPrimaryClip(manager: ClipboardManager, clip: ClipData?) {
            doFilePrinter("setPrimaryClip", "设置剪贴板内容-setPrimaryClip")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return
            }
            clip?.let { manager.setPrimaryClip(it) }

        }

        @PrivacyMethodProxy(
            originalClass = ClipboardManager::class,
            originalMethod = "setText",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun setText(manager: ClipboardManager, clip: CharSequence?) {
            doFilePrinter("setText", "设置剪贴板内容-setText")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return
            }
            manager.text = clip
        }

        /**
         * WIFI的SSID
         */
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = WifiInfo::class,
            originalMethod = "getSSID",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getSSID(manager: WifiInfo): String? {
            if (PrivacySentry.Privacy.inDangerousState()) {
                doFilePrinter("getSSID", "SSID")
                return ""
            }

            var key = "getSSID"
            doFilePrinter("getSSID", "SSID")
//            return CachePrivacyManager.Manager.loadWithTimeMemoryCache(
//                key,
//                "getSSID",
//                "",
//                duration = CacheUtils.Utils.MINUTE * 5
//                ) { manager.ssid }
            return manager.ssid
        }

        /**
         * WIFI的SSID
         */
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = WifiInfo::class,
            originalMethod = "getBSSID",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getBSSID(manager: WifiInfo): String? {

            if (PrivacySentry.Privacy.inDangerousState()) {
                doFilePrinter("getBSSID", "getBSSID")
                return ""
            }

            var key = "getBSSID"
            doFilePrinter("getBSSID", "getBSSID")
//            return CachePrivacyManager.Manager.loadWithTimeMemoryCache(
//                key,
//                "getBSSID",
//                "",
//                duration = CacheUtils.Utils.MINUTE * 5
//            ) { manager.ssid }
            return manager.bssid
        }

        /**
         * WIFI扫描结果
         */
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = WifiManager::class,
            originalMethod = "getScanResults",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getScanResults(manager: WifiManager): List<ScanResult>? {
            if (PrivacySentry.Privacy.inDangerousState()) {
                doFilePrinter("getScanResults", "WIFI扫描结果")
                return emptyList()
            }
            doFilePrinter("getScanResults", "WIFI扫描结果")
            var key = "getScanResults"
            return CachePrivacyManager.Manager.loadWithTimeMemoryCache(
                key,
                "getScanResults",
                emptyList(),
                duration = CacheUtils.Utils.MINUTE * 5
            ) { manager.scanResults }
        }

        /**
         * DHCP信息
         */
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = WifiManager::class,
            originalMethod = "getDhcpInfo",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun getDhcpInfo(manager: WifiManager): DhcpInfo? {
            doFilePrinter("getDhcpInfo", "DHCP地址")
            if (PrivacySentry.Privacy.inDangerousState()) {
                return null
            }
            return manager.getDhcpInfo()
        }

        /**
         * DHCP信息
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
            if (PrivacySentry.Privacy.inDangerousState()) {
                return emptyList()
            }
            return manager.getConfiguredNetworks()
        }


        /**
         * 位置信息
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
            var key = "getLastKnownLocation_${provider}"
            if (PrivacySentry.Privacy.inDangerousState()) {
                doFilePrinter("getLastKnownLocation", "上一次的位置信息")
                // 这里直接写空可能有风险
                return null
            }

            var locationStr = CachePrivacyManager.Manager.loadWithTimeDiskCache(
                key,
                "上一次的位置信息",
                ""
            ) { PrivacyUtil.Util.formatLocation(manager.getLastKnownLocation(provider)) }

            var location: Location? = null
            locationStr.also {
                location = PrivacyUtil.Util.formatLocation(it)
            }
            if (location == null) {
                return manager.getLastKnownLocation(provider)
            }
            return location
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
            if (PrivacySentry.Privacy.inDangerousState()) {
                return
            }
            manager.requestLocationUpdates(provider, minTime, minDistance, listener)
        }


        var objectMacLock = Object()
        var objectHardMacLock = Object()
        var objectSNLock = Object()
        var objectAndroidIdLock = Object()
        var objectExternalStorageDirectoryLock = Object()


        @PrivacyMethodProxy(
            originalClass = WifiInfo::class,
            originalMethod = "getMacAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getMacAddress(manager: WifiInfo): String? {
            var key = "WifiInfo-getMacAddress"

            if (PrivacySentry.Privacy.inDangerousState()) {
                doFilePrinter(
                    key,
                    "mac地址-getMacAddress"
                )
                return ""
            }

            synchronized(objectMacLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "mac地址-getMacAddress",
                    ""
                ) { manager.getMacAddress() }
            }
        }


        @PrivacyMethodProxy(
            originalClass = NetworkInterface::class,
            originalMethod = "getHardwareAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getHardwareAddress(manager: NetworkInterface): ByteArray? {
            var key = "NetworkInterface-getHardwareAddress"

            if (PrivacySentry.Privacy.inDangerousState()) {
                doFilePrinter(
                    key,
                    "mac地址-getHardwareAddress"
                )
                return ByteArray(1)
            }
            synchronized(objectHardMacLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "mac地址-getHardwareAddress",
                    ""
                ) { manager.hardwareAddress.toString() }.toByteArray()
            }
        }

        var objectBluetoothLock = Object()

        @PrivacyMethodProxy(
            originalClass = BluetoothAdapter::class,
            originalMethod = "getAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getAddress(manager: BluetoothAdapter): String? {
            var key = "BluetoothAdapter-getAddress"

            if (PrivacySentry.Privacy.inDangerousState()) {
                doFilePrinter(key, "蓝牙地址-getAddress")
                return ""
            }
            synchronized(objectBluetoothLock) {
                return CachePrivacyManager.Manager.loadWithMemoryCache(
                    key,
                    "蓝牙地址-getAddress",
                    ""
                ) { manager.address }
            }
        }


        @PrivacyMethodProxy(
            originalClass = Inet4Address::class,
            originalMethod = "getAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getAddress(manager: Inet4Address): ByteArray? {
            var key = "ip地址-getAddress"

//            if (PrivacySentry.Privacy.inDangerousState()) {
//                doFilePrinter(key, "ip地址-getAddress")
//                return ByteArray(1)
//            }
            var address = manager.address
            doFilePrinter(
                key,
                "ip地址-getAddress-${manager.address ?: ""} , address is ${address ?: ""}"
            )
            return address
        }

        @PrivacyMethodProxy(
            originalClass = InetAddress::class,
            originalMethod = "getAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getAddress(manager: InetAddress): ByteArray? {
            var key = "ip地址-getAddress"

//            if (PrivacySentry.Privacy.inDangerousState()) {
//                doFilePrinter(key, "ip地址-getAddress")
//                return ByteArray(1)
//            }
            var address = manager.address
            doFilePrinter(
                key,
                "ip地址-getAddress-${manager.address ?: ""} , address is ${address ?: ""} "
            )
            return address
        }

        @PrivacyMethodProxy(
            originalClass = Inet4Address::class,
            originalMethod = "getHostAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getHostAddress(manager: Inet4Address): String? {
            var key = "ip地址-getHostAddress"

//            if (PrivacySentry.Privacy.inDangerousState()) {
//                doFilePrinter(key, "ip地址-getHostAddress")
//                return ""
//            }

            var address = manager.hostAddress
            doFilePrinter(
                key,
                "ip地址-getHostAddress-${manager.hostAddress ?: ""} , address is ${address ?: ""}"
            )
            return address
        }

        @PrivacyMethodProxy(
            originalClass = InetAddress::class,
            originalMethod = "getHostAddress",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun getHostAddress(manager: InetAddress): String? {
            var key = "ip地址-getHostAddress"

//            if (PrivacySentry.Privacy.inDangerousState()) {
//                doFilePrinter(key, "ip地址-getHostAddress")
//                return ""
//            }

            var address = manager.hostAddress
            doFilePrinter(
                key,
                "ip地址-getHostAddress-${manager.hostAddress ?: ""} , address is ${address ?: ""}"
            )
            return address
        }

        @PrivacyMethodProxy(
            originalClass = Settings.Secure::class,
            originalMethod = "getString",
            originalOpcode = MethodInvokeOpcode.INVOKESTATIC
        )
        @JvmStatic
        fun getString(contentResolver: ContentResolver?, type: String?): String? {
            var key = "Secure-getString-$type"
            if (!"android_id".equals(type)) {
                return Settings.Secure.getString(
                    contentResolver,
                    type
                )
            }
            if (PrivacySentry.Privacy.inDangerousState()) {
                doFilePrinter(
                    "getString",
                    "系统信息",
                    args = type
                )
                return ""
            }
            synchronized(objectAndroidIdLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "getString-系统信息",
                    ""
                ) {
                    Settings.Secure.getString(
                        contentResolver,
                        type
                    )
                }
            }
        }


        @PrivacyMethodProxy(
            originalClass = Settings.System::class,
            originalMethod = "getString",
            originalOpcode = MethodInvokeOpcode.INVOKESTATIC
        )
        @JvmStatic
        fun getStringSystem(contentResolver: ContentResolver?, type: String?): String? {
            return getString(contentResolver, type)
        }

        @PrivacyMethodProxy(
            originalClass = android.os.Build::class,
            originalMethod = "getSerial",
            originalOpcode = MethodInvokeOpcode.INVOKESTATIC
        )
        @JvmStatic
        fun getSerial(): String? {
            var result = ""
            var key = "getSerial"
            if (PrivacySentry.Privacy.inDangerousState()) {
                doFilePrinter("getSerial", "Serial")
                return ""
            }
            synchronized(objectSNLock) {
                return CachePrivacyManager.Manager.loadWithDiskCache(
                    key,
                    "getSerial",
                    ""
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Build.getSerial()
                    } else {
                        Build.SERIAL
                    }
                }
            }
        }

        @PrivacyMethodProxy(
            originalClass = android.os.Environment::class,
            originalMethod = "getExternalStorageDirectory",
            originalOpcode = MethodInvokeOpcode.INVOKESTATIC
        )
        @JvmStatic
        fun getExternalStorageDirectory(): File? {
            var result: File? = null
            var key = "externalStorageDirectory"
            if (PrivacySentry.Privacy.inDangerousState()) {
                doFilePrinter("getExternalStorageDirectory", key)
            }
            synchronized(objectExternalStorageDirectoryLock) {
                result = CachePrivacyManager.Manager.loadWithMemoryCache<File>(
                    key,
                    "getExternalStorageDirectory",
                    File("")
                ) {
                    Environment.getExternalStorageDirectory()
                }
            }
            return result
        }

        // 拦截获取系统设备，简直离谱，这个也不能重复获取
        @JvmStatic
        fun getBrand(): String? {
            PrivacyLog.i("getBrand")
            var key = "getBrand"
            return CachePrivacyManager.Manager.loadWithMemoryCache(
                key,
                "getBrand",
                ""
            ) {
                PrivacyLog.i("getBrand Value")
                Build.BRAND
            }
        }
    }
}
