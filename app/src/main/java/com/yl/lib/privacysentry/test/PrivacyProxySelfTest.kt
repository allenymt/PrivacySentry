package com.yl.lib.privacysentry.test

import android.app.ActivityManager
import android.content.ContentResolver
import android.os.Build
import android.provider.Settings
import androidx.annotation.Keep
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import com.yl.lib.privacy_annotation.PrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyMethodProxy
import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil.Util.doFilePrinter

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

        var objectAndroidIdLock = Object()
        @PrivacyMethodProxy(
            originalClass = Settings.Secure::class,
            originalMethod = "getString",
            originalOpcode = MethodInvokeOpcode.INVOKESTATIC
        )
        @JvmStatic
        fun getString(contentResolver: ContentResolver?, type: String?): String? {
            var key = "Secure-getString-$type"
            if (!"android_id".equals(type)) {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                    Settings.Secure.getString(
                        contentResolver,
                        type
                    )
                } else {
                    ""
                }
            }
            if (PrivacySentry.Privacy.getBuilder()?.isVisitorModel() == true) {
                doFilePrinter(
                    "getString",
                    "系统信息",
                    args = type,
                    bVisitorModel = true
                )
                return ""
            }
            synchronized(objectAndroidIdLock) {
                var hasValue = PrivacyProxyUtil.Util.hasReadStaticParam(key)
                if (!hasValue) {
                    doFilePrinter("getString", "系统信息", args = type)
                    var result = ""
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                            result = Settings.Secure.getString(
                                contentResolver,
                                type
                            )
                        }
                    } catch (e: Throwable) {
                        //不管有没有申请，部分机子直接跑异常
                        throw e
                    } finally {
                        PrivacyProxyUtil.Util.putCacheStaticParam(result ?: "", key)
                    }
                    return result
                } else {
                    doFilePrinter(
                        "getString",
                        "系统信息",
                        args = type,
                        bCache = true
                    )
                    return PrivacyProxyUtil.Util.getCacheStaticParam("", key)
                }
            }
        }
    }


}