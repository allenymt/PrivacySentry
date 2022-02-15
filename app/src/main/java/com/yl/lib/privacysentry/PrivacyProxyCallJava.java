package com.yl.lib.privacysentry;

import android.app.ActivityManager;

import com.yl.lib.privacy_annotation.MethodInvokeOpcode;
import com.yl.lib.privacy_annotation.PrivacyClassProxy;
import com.yl.lib.privacy_annotation.PrivacyMethodProxy;
import com.yl.lib.sentry.hook.PrivacySentry;
import com.yl.lib.sentry.hook.printer.BasePrinter;
import com.yl.lib.sentry.hook.util.PrivacyUtil;

import java.util.List;

/**
 * @author yulun
 * @sinice 2022-01-05 19:31
 */
@PrivacyClassProxy
public class PrivacyProxyCallJava {

    @PrivacyMethodProxy(
            originalClass = ActivityManager.class,
            originalMethod = "getRunningTasks",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
    )
    public static List<ActivityManager.RunningTaskInfo> getRunningTasks(
            ActivityManager manager,
            int maxNum
    ) {
        doFilePrinter("getRunningTasks", "获取当前运行中的任务", "");
        return manager.getRunningTasks(maxNum);
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
