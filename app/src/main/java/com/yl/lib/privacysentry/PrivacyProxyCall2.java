package com.yl.lib.privacysentry;

import android.app.ActivityManager;

import com.yl.lib.privacy_annotation.MethodInvokeOpcode;
import com.yl.lib.privacy_annotation.PrivacyClassProxy;
import com.yl.lib.privacy_annotation.PrivacyMethodProxy;

import java.util.List;

/**
 * @author yulun
 * @sinice 2022-01-05 19:31
 */
@PrivacyClassProxy
public class PrivacyProxyCall2 {

    @PrivacyMethodProxy(
            originalClass = ActivityManager.class,
            originalMethod = "getRunningTasks",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
            documentDesc = "获取当前运行中的任务"
    )
    public static List<ActivityManager.RunningTaskInfo> getRunningTasks(
            ActivityManager manager,
            int maxNum
    ) {
//            doFilePrinter("getRunningTasks")
        return manager.getRunningTasks(maxNum);
    }
}
