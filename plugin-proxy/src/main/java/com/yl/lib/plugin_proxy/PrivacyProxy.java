package com.yl.lib.plugin_proxy;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiInfo;
import android.telephony.TelephonyManager;

import java.net.NetworkInterface;
import java.util.List;

/**
 * @author yulun
 * @sinice 2021-12-22 17:29
 */
public class PrivacyProxy {
    private static IPrivacyProxy iPrivacyProxy = new PrivacyProxyCall();

    public static void setPrivacyProxy(IPrivacyProxy _iPrivacyProxy) {
        iPrivacyProxy = _iPrivacyProxy;
    }

    public static List<ActivityManager.RunningTaskInfo> getRunningTasks(
            ActivityManager manager,
            int maxNum
    ) {
        return iPrivacyProxy.getRunningTasks(manager, maxNum);
    }

    public static List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses(ActivityManager manager) {
        return iPrivacyProxy.getRunningAppProcesses(manager);
    }

    public static List<PackageInfo> getInstalledPackages(PackageManager manager, int flags) {
        return iPrivacyProxy.getInstalledPackages(manager, flags);
    }

    public static List<ResolveInfo> queryIntentActivities(
            PackageManager manager,
            Intent intent,
            int flags
    ) {
        return iPrivacyProxy.queryIntentActivities(manager, intent, flags);
    }

    public static List<ResolveInfo> queryIntentActivityOptions(
            PackageManager manager,
            ComponentName caller,
            Intent[] specifics,
            Intent intent,
            int flags
    ) {
        return iPrivacyProxy.queryIntentActivityOptions(
                manager,
                caller,
                specifics,
                intent,
                flags
        );
    }

    public static String getMeid(TelephonyManager manager) {
        return iPrivacyProxy.getMeid(
                manager
        );
    }

    public static String getMeid(TelephonyManager manager, int index) {
        return iPrivacyProxy.getMeid(
                manager, index
        );
    }

    public static String getDeviceId(TelephonyManager manager) {
        return iPrivacyProxy.getDeviceId(
                manager
        );
    }

    public static String getDeviceId(TelephonyManager manager, int index) {
        return iPrivacyProxy.getDeviceId(
                manager, index
        );
    }

    public static String getSubscriberId(TelephonyManager manager) {
        return iPrivacyProxy.getSubscriberId(
                manager
        );
    }

    //参数丢失，不过APP本身就调用不到
    public static String getSubscriberId(TelephonyManager manager, int index) {
        return iPrivacyProxy.getSubscriberId(
                manager, index
        );
    }

    public static String getImei(TelephonyManager manager) {
        return iPrivacyProxy.getImei(
                manager
        );
    }

    public static String getImei(TelephonyManager manager, int index) {
        return iPrivacyProxy.getImei(
                manager, index
        );
    }

    public static String getSimSerialNumber(TelephonyManager manager) {
        return iPrivacyProxy.getSimSerialNumber(
                manager
        );
    }

    public static String getSimSerialNumber(TelephonyManager manager, int index) {
        return iPrivacyProxy.getSimSerialNumber(
                manager, index
        );
    }

    public static ClipData getPrimaryClip(ClipboardManager manager) {
        return iPrivacyProxy.getPrimaryClip(
                manager
        );
    }

    public static ClipDescription getPrimaryClipDescription(ClipboardManager manager) {
        return iPrivacyProxy.getPrimaryClipDescription(
                manager
        );
    }

    public static CharSequence getText(ClipboardManager manager) {
        return iPrivacyProxy.getText(
                manager
        );
    }

    public static void setPrimaryClip(ClipboardManager manager, ClipData clip) {
        iPrivacyProxy.setPrimaryClip(
                manager, clip
        );
    }

    public static void setText(ClipboardManager manager, CharSequence clip) {
        iPrivacyProxy.setText(manager, clip);
    }

    public static String getMacAddress(WifiInfo manager) {
        return iPrivacyProxy.getMacAddress(manager);
    }

    public static byte[] getHardwareAddress(NetworkInterface manager) {
        return iPrivacyProxy.getHardwareAddress(manager);
    }

    public static String getAddress(BluetoothAdapter manager) {
        return iPrivacyProxy.getAddress(manager);
    }

    public static String getSecureString(ContentResolver contentResolver, String type) {
        return iPrivacyProxy.getSecureString(contentResolver, type);
    }
}
