package com.yl.lib.plugin_proxy

import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.wifi.WifiInfo
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import com.yl.lib.sentry.base.HookMethodItem
import com.yl.lib.sentry.base.HookMethodManager
import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.util.PrivacyUtil
import java.net.NetworkInterface

/**
 * @author yulun
 * @sinice 2021-12-22 14:23
 */
class PrivacyProxyCall : IPrivacyProxy {

    override fun getRunningTasks(
        manager: ActivityManager,
        maxNum: Int
    ): List<ActivityManager.RunningTaskInfo?>? {
        doFilePrinter("getRunningTasks")
        return manager.getRunningTasks(maxNum)
    }

    override fun getRunningAppProcesses(manager: ActivityManager): List<ActivityManager.RunningAppProcessInfo> {
        doFilePrinter("getRunningAppProcesses")
        return manager.getRunningAppProcesses()
    }

    override fun getInstalledPackages(manager: PackageManager, flags: Int): List<PackageInfo> {
        doFilePrinter("getInstalledPackages")
        return manager.getInstalledPackages(flags)
    }

    override fun queryIntentActivities(
        manager: PackageManager,
        intent: Intent,
        flags: Int
    ): List<ResolveInfo> {
        doFilePrinter("queryIntentActivities")
        return manager.queryIntentActivities(intent, flags)
    }

    override fun queryIntentActivityOptions(
        manager: PackageManager,
        caller: ComponentName?,
        specifics: Array<Intent?>?,
        intent: Intent,
        flags: Int
    ): List<ResolveInfo> {
        doFilePrinter("queryIntentActivityOptions")
        return manager.queryIntentActivityOptions(caller, specifics, intent, flags)
    }

    override fun getMeid(manager: TelephonyManager): String? {
        doFilePrinter("getMeid", "()Ljava/lang/String;")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.getMeid()
        } else {
            ""
        }
    }

    override fun getMeid(manager: TelephonyManager, index: Int): String? {
        doFilePrinter("getMeid", "(I)Ljava/lang/String;")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.getMeid(index)
        } else {
            ""
        }
    }

    override fun getDeviceId(manager: TelephonyManager): String? {
        doFilePrinter("getDeviceId", "()Ljava/lang/String;")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.getDeviceId()
        } else {
            ""
        }
    }

    override fun getDeviceId(manager: TelephonyManager, index: Int): String? {
        doFilePrinter("getDeviceId", "(I)Ljava/lang/String;")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.getDeviceId(index)
        } else {
            ""
        }
    }

    override fun getSubscriberId(manager: TelephonyManager): String? {
        doFilePrinter("getSubscriberId")
        return manager.subscriberId
    }

    //参数丢失，不过APP本身就调用不到
    override fun getSubscriberId(manager: TelephonyManager, index: Int): String? {
        doFilePrinter("getSubscriberId")
        return manager.subscriberId
    }

    override fun getImei(manager: TelephonyManager): String? {
        doFilePrinter("getImei", "()Ljava/lang/String;")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.getImei()
        } else {
            ""
        }
    }

    override fun getImei(manager: TelephonyManager, index: Int): String? {
        doFilePrinter("getImei", "(I)Ljava/lang/String;")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.getImei(index)
        } else {
            ""
        }
    }

    override fun getSimSerialNumber(manager: TelephonyManager): String? {
        doFilePrinter("getSimSerialNumber")
        return manager.getSimSerialNumber()
    }

    //参数丢失，不过APP本身就调用不到
    override fun getSimSerialNumber(manager: TelephonyManager, index: Int): String? {
        doFilePrinter("getSimSerialNumber")
        return manager.getSimSerialNumber()
    }

    override fun getPrimaryClip(manager: ClipboardManager): ClipData? {
        doFilePrinter("getPrimaryClip")
        return manager.primaryClip
    }

    override fun getPrimaryClipDescription(manager: ClipboardManager): ClipDescription? {
        doFilePrinter("getPrimaryClipDescription")
        return manager.primaryClipDescription
    }

    override fun getText(manager: ClipboardManager): CharSequence? {
        doFilePrinter("getText")
        return manager.text
    }

    override fun setPrimaryClip(manager: ClipboardManager, clip: ClipData) {
        doFilePrinter("setPrimaryClip")
        manager.setPrimaryClip(clip)
    }

    override fun setText(manager: ClipboardManager, clip: CharSequence) {
        doFilePrinter("setText")
        manager.text = clip
    }

    override fun getMacAddress(manager: WifiInfo): String? {
        doFilePrinter("getMacAddress")
        return manager.getMacAddress();
    }

    override fun getHardwareAddress(manager: NetworkInterface): ByteArray? {
        doFilePrinter("getHardwareAddress")
        return manager.hardwareAddress
    }

    override fun getAddress(manager: BluetoothAdapter): String? {
        doFilePrinter("getAddress")
        return manager.address ?: ""
    }

    override fun getString(contentResolver: ContentResolver?, type: String?): String? {
        var result = ""
        try {
            doFilePrinter("getString", args = type)
            result = Settings.Secure.getString(
                contentResolver,
                type
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun doFilePrinter(funName: String, methodReturnDesc: String = "", args: String? = "") {
        var hookMethodItem: HookMethodItem? = HookMethodManager.MANAGER.findHookItemByName(
            funName,
            methodReturnDesc = methodReturnDesc
        )
        hookMethodItem?.let {
            PrivacySentry.Privacy.getBuilder()?.getPrinterList()?.forEach {
                it.filePrint(
                    hookMethodItem?.methodName!!,
                    hookMethodItem?.methodDesc!! + "--参数: $args",
                    PrivacyUtil.Util.getStackTrace()
                )
            }
        }
    }
}