package com.yl.lib.privacy_proxy

import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.wifi.WifiInfo
import android.os.Build
import android.provider.Settings
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
                return ArrayList<ActivityManager.RunningTaskInfo>()
            }
            return manager.getRunningTasks(maxNum)
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
                return ArrayList<ActivityManager.RunningAppProcessInfo>()
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
                return ArrayList<PackageInfo>()
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
                return ArrayList<ResolveInfo>()
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
                return ArrayList<ResolveInfo>()
            }
            return manager.queryIntentActivityOptions(caller, specifics, intent, flags)
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