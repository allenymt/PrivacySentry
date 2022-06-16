package com.yl.lib.privacy_test;

import android.app.ActivityManager;

import androidx.annotation.Keep;

import com.yl.lib.privacy_annotation.MethodInvokeOpcode;
import com.yl.lib.privacy_annotation.PrivacyClassProxy;
import com.yl.lib.privacy_annotation.PrivacyMethodProxy;

import java.util.List;

import kotlin.jvm.JvmStatic;

/**
 * @author yulun
 * @since 2022-06-15 20:32
 */
@PrivacyClassProxy
@Keep
public class PrivacyProxySelfTest2 {

    // 这个方法的注册放在了PrivacyProxyCall2中，提供了一个java注册的例子
    @PrivacyMethodProxy(
            originalClass = ActivityManager.class,
            originalMethod = "getRunningTasks",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
    )
    @JvmStatic
    public static List<ActivityManager.RunningTaskInfo> getRunningTasks456(
            ActivityManager manager,
            int maxNum
    ) {
        android.util.Log.i("yulun", "PrivacyProxySelfTest2");
        return manager.getRunningTasks(maxNum);
    }
}
