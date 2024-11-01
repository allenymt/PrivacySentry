package com.yl.lib.privacysentry.test;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.yl.lib.privacy_annotation.MethodInvokeOpcode;
import com.yl.lib.privacy_annotation.PrivacyClassProxy;
import com.yl.lib.privacy_annotation.PrivacyMethodProxy;
import com.yl.lib.sentry.hook.PrivacySentry;
import com.yl.lib.sentry.hook.printer.BasePrinter;
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil;
import com.yl.lib.sentry.hook.util.PrivacyUtil;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.JvmStatic;

/**
 * @author yulun
 * @sinice 2022-01-05 19:31
 */
@PrivacyClassProxy
public class PrivacyProxyCallJava {

    public static List<ActivityManager.RunningTaskInfo> getRunningTasks(
            ActivityManager manager,
            int maxNum
    ) {
        doFilePrinter("getRunningTasks", "获取当前运行中的任务", "");
        return manager.getRunningTasks(maxNum);
    }

    @PrivacyMethodProxy(
            originalClass = SharedPreferences.Editor.class,
            originalMethod = "putString",
            originalOpcode = MethodInvokeOpcode.INVOKEINTERFACE
    )
    public static SharedPreferences.Editor putString(SharedPreferences.Editor editor, String var1,String var2) {
       return editor.putString(var1, var2);
    }

    private static void doFilePrinter(
            String funName,
            String methodDocumentDesc,
            String args
    ) {
        for (BasePrinter p :
                PrivacySentry.Privacy.INSTANCE.getBuilder().getPrinterList()) {
            p.filePrint(
                    funName,
                    methodDocumentDesc + (args.isEmpty() ? "" : "--参数: " + args),
                    PrivacyUtil.Util.INSTANCE.getStackTrace()
            );
        }
    }
}
