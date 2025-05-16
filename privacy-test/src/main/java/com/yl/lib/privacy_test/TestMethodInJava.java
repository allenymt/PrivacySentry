package com.yl.lib.privacy_test;

import android.app.ActivityManager;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

/**
 * @author yulun
 * @sinice 2021-12-22 19:46
 */
public class TestMethodInJava {
    public static String getAndroidId(Context context) {
        String androidId = "" + Settings.Secure.getString(context.getContentResolver(), "android_id");
        return androidId;
    }

    public static String getAndroidId2(Context context) {
        String androidId = "" + Settings.Secure.getString(context.getContentResolver(), "android_id");
        return androidId;
    }

    public static String getAndroidIdSystem(Context context) {
        String androidId = "" + Settings.System.getString(context.getContentResolver(), "android_id");
        return androidId;
    }

    public static void getSubscriberId(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
            String mImsi = tm.getSubscriberId();
            if (null == mImsi || mImsi.trim().length() == 0) {
                mImsi = "000000";
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }
    }



    public static void testRunningProcess(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        manager.getRunningAppProcesses();
    }

    public static void testRunningTask(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        manager.getRunningTasks(100);
    }
}
