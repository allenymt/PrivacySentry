package com.yl.lib.plugin_proxy

import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.wifi.WifiInfo
import android.telephony.TelephonyManager
import java.net.NetworkInterface

/**
 * @author yulun
 * @sinice 2021-12-22 11:12
 * 实现所有 asm-hook的接口，方便方法替换时统一调用
 */
interface IPrivacyProxy {
    fun getRunningTasks(
        manager: ActivityManager,
        maxNum: Int
    ): List<ActivityManager.RunningTaskInfo?>?

    fun getRunningAppProcesses(manager: ActivityManager): List<ActivityManager.RunningAppProcessInfo>

    fun getInstalledPackages(manager: PackageManager, flags: Int): List<PackageInfo>

    fun queryIntentActivities(
        manager: PackageManager,
        intent: Intent,
        flags: Int
    ): List<ResolveInfo>

    fun queryIntentActivityOptions(
        manager: PackageManager,
        caller: ComponentName?,
        specifics: Array<Intent?>?, intent: Intent, flags: Int
    ): List<ResolveInfo>

    fun getMeid(manager: TelephonyManager): String?

    fun getMeid(manager: TelephonyManager, index: Int): String?

    fun getDeviceId(manager: TelephonyManager): String?

    fun getDeviceId(manager: TelephonyManager, index: Int): String?

    fun getSubscriberId(manager: TelephonyManager): String?

    fun getSubscriberId(manager: TelephonyManager, index: Int): String?

    fun getImei(manager: TelephonyManager): String?

    fun getImei(manager: TelephonyManager, index: Int): String?

    fun getSimSerialNumber(manager: TelephonyManager): String?

    fun getSimSerialNumber(manager: TelephonyManager, index: Int): String?

    fun getPrimaryClip(manager: ClipboardManager): ClipData?

    fun getPrimaryClipDescription(manager: ClipboardManager): ClipDescription?

    fun getText(manager: ClipboardManager): CharSequence?

    fun setPrimaryClip(manager: ClipboardManager, clip: ClipData)

    fun setText(manager: ClipboardManager, clip: CharSequence)

    fun getMacAddress(manager: WifiInfo): String?

    fun getHardwareAddress(manager: NetworkInterface): ByteArray?

    fun getAddress(manager: BluetoothAdapter): String?

    fun getSecureString(contentResolver: ContentResolver?, type: String?): String?
}