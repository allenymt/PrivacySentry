package com.yl.lib.sentry.hook.util

import android.content.pm.PackageManager
import com.yl.lib.sentry.hook.PrivacySentry

/**
 * @author yulun
 * @since 2022-01-13 17:58
 */
class PrivacyProxyUtil {
    object Util {
        fun doFilePrinter(
            funName: String,
            methodDocumentDesc: String = "",
            args: String? = "",
            bVisitorModel: Boolean = false,
            bCache: Boolean = false
        ) {
            if (bVisitorModel || PrivacySentry.Privacy.inDangerousState()) {
                PrivacyLog.e("disable print file: funName is $funName methodDocumentDesc is $methodDocumentDesc,isVisitorModel=true ${PrivacyUtil.Util.getStackTrace()}")
                return
            }

            PrivacySentry.Privacy.getBuilder()?.getPrinterList()?.forEach {
                it.filePrint(
                    funName + "-\n线程名: ${Thread.currentThread().name}",
                    (if (bCache) "命中缓存--" else "") + methodDocumentDesc + if (args?.isNotEmpty() == true) "--参数: $args" else "",
                    PrivacyUtil.Util.getStackTrace()
                )
            }
        }

        /**
         * 检查运行时权限
         * @param permission String
         * @return Boolean
         */
        fun checkPermission(permission: String): Boolean {
            val localPackageManager: PackageManager? =
                PrivacySentry.Privacy.getContext()?.packageManager
            return localPackageManager?.checkPermission(
                permission,
                PrivacySentry.Privacy.getContext()?.packageName ?: ""
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

}