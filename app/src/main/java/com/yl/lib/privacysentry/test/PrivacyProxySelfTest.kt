package com.yl.lib.privacysentry.test

import android.app.ActivityManager
import android.content.SharedPreferences
import androidx.annotation.Keep
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import com.yl.lib.privacy_annotation.PrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyMethodProxy
import com.yl.lib.sentry.hook.util.PrivacyLog
import java.net.HttpURLConnection

/**
 * @author yulun
 * @since 2022-06-15 20:01
 */
@Keep
class PrivacyProxySelfTest {
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
        fun getRunningTasks123(
            manager: ActivityManager,
            maxNum: Int
        ): List<ActivityManager.RunningTaskInfo?>? {
            return manager.getRunningTasks(maxNum)
        }


        @PrivacyMethodProxy(
            originalClass = HttpURLConnection::class,
            originalMethod = "connect",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun connect(httpURLConnection: HttpURLConnection) {
            PrivacyLog.i("HttpURLConnection connect")
            httpURLConnection.connect()
        }

        @PrivacyMethodProxy(
            originalClass = SharedPreferences.Editor::class,
            originalMethod = "apply",
            originalOpcode = MethodInvokeOpcode.INVOKEINTERFACE
        )
        @JvmStatic
        fun apply(
            editor: SharedPreferences.Editor
        ): Unit {
            editor.apply()
        }
    }
}