package com.yl.lib.privacy_proxy

import android.app.Activity
import android.os.Build
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import com.yl.lib.privacy_annotation.PrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyMethodProxy
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil
import com.yl.lib.sentry.hook.util.ReflectUtils

/**
 * @author yulun
 * @since 2022-11-09 17:41
 * 代理请求权限
 */
@Keep
class PrivacyPermissionProxy {

    @PrivacyClassProxy
    @Keep
    object Proxy {

        // INVOKESPECIAL androidx/appcompat/app/AppCompatActivity.requestPermissions ([Ljava/lang/String;I)V
        // 代理当前类调用super.requestPermissions
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = Any::class,
            originalMethod = "requestPermissions",
            originalOpcode = MethodInvokeOpcode.INVOKESPECIAL,
            ignoreClass = true
        )
        fun requestPermissionsSuper(obj: Any, permissions: Array<String?>, requestCode: Int) {
            PrivacyProxyUtil.Util.doFilePrinter(
                "requestPermissions",
                methodDocumentDesc = "${obj.javaClass.name}-INVOKESPECIAL-请求权限"
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ReflectUtils.Utils.invokeMethod<Unit>(
                    obj, "requestPermissions", arrayOf(
                        Array<String>::class.java, Integer.TYPE
                    ), arrayOf<Any?>(permissions, requestCode)
                )
            }
        }


        // INVOKEVIRTUAL com/yl/lib/privacysentry/MainActivity.requestPermissions ([Ljava/lang/String;I)V
        // 代理当前类调用requestPermissions
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = Any::class,
            originalMethod = "requestPermissions",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL,
            ignoreClass = true
        )
        fun requestPermissions(any: Any, permissions: Array<String?>, requestCode: Int) {
            PrivacyProxyUtil.Util.doFilePrinter(
                "requestPermissions",
                methodDocumentDesc = "${any.javaClass.name}-INVOKEVIRTUAL-请求权限"
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (any is Activity) {
                    // 这里可能会多次调用代理方法
                    any.requestPermissions(permissions, requestCode)
                } else if (any is Fragment) {
                    // 这里可能会多次调用代理方法
                    any.requestPermissions(permissions, requestCode)
                } else {
                    ReflectUtils.Utils.invokeMethod<Unit>(
                        any, "requestPermissions", arrayOf(
                            Array<String>::class.java, Integer.TYPE
                        ), arrayOf<Any?>(permissions, requestCode)
                    )
                }
            }
        }

    }
}