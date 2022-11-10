package com.yl.lib.privacy_proxy

import android.app.Activity
import android.os.Build
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import com.yl.lib.privacy_annotation.PrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyMethodProxy
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil

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
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = Activity::class,
            originalMethod = "requestPermissions",
            originalOpcode = MethodInvokeOpcode.INVOKESPECIAL
        )
        fun requestPermissions(activity: Activity, permissions: Array<String?>, requestCode: Int) {
            PrivacyProxyUtil.Util.doFilePrinter(
                "requestPermissions",
                methodDocumentDesc = "Activity-请求权限"
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(permissions, requestCode)
            }
        }


        // INVOKEVIRTUAL com/yl/lib/privacysentry/MainActivity.requestPermissions ([Ljava/lang/String;I)V
        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = Any::class,
            originalMethod = "requestPermissions",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        fun requestPermissions(any: Any, permissions: Array<String?>, requestCode: Int) {
            PrivacyProxyUtil.Util.doFilePrinter(
                "requestPermissions",
                methodDocumentDesc = "Activity-请求权限"
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (any is Activity) {
                    any.requestPermissions(permissions, requestCode)
                }
            }
        }

        @JvmStatic
        @PrivacyMethodProxy(
            originalClass = Fragment::class,
            originalMethod = "requestPermissions",
            originalOpcode = MethodInvokeOpcode.INVOKESPECIAL
        )
        fun requestPermissions(fragment: Fragment, permissions: Array<String?>, requestCode: Int) {
            PrivacyProxyUtil.Util.doFilePrinter(
                "requestPermissions",
                methodDocumentDesc = "Fragment-请求权限"
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragment.requestPermissions(permissions, requestCode)
            }
        }
    }
}