package com.yl.lib.privacy_proxy

import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.util.PrivacyLog
import com.yl.lib.sentry.hook.util.PrivacyUtil

/**
 * @author yulun
 * @since 2022-01-13 17:58
 */
class PrivacyProxyUtil {
    object Util{
        fun doFilePrinter(
            funName: String,
            methodDocumentDesc: String = "",
            args: String? = ""
        ) {
            if (PrivacySentry.Privacy.getBuilder()?.isEnableFileResult() == false) {
                PrivacyLog.e("disable print file: funName is $funName methodDocumentDesc is $methodDocumentDesc")
                return
            }
            PrivacySentry.Privacy.getBuilder()?.getPrinterList()?.forEach {
                it.filePrint(
                    funName,
                    methodDocumentDesc + if (args?.isNotEmpty() == true) "--参数: $args" else "",
                    PrivacyUtil.Util.getStackTrace()
                )
            }
        }
    }

}