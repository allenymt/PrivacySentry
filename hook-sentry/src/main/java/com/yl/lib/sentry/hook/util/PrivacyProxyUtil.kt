package com.yl.lib.sentry.hook.util

import android.content.pm.PackageManager
import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.printer.PrivacyFunBean
import com.yl.lib.sentry.hook.watcher.PrivacyDataManager

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
            bCache: Boolean = false
        ) {
            var funName = funName + "-\n线程名: ${Thread.currentThread().name}"
            var funAlias =
                (if (bCache) "命中缓存--" else "") + methodDocumentDesc + if (args?.isNotEmpty() == true) "--参数: $args" else ""
            var msg = PrivacyUtil.Util.getStackTrace()

            // 这里不再拦截，交给printer自己拦截
            if (!PrivacySentry.Privacy.hasInit()) {
                PrivacyDataManager.Manager.addStickData(PrivacyFunBean(funName, funAlias, msg, 1))
            }

            PrivacySentry.Privacy.getBuilder()?.getPrinterList()?.forEach {
                it.filePrint(
                    funName,
                    funAlias,
                    msg
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