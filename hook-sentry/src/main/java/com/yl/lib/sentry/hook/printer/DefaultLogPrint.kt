package com.yl.lib.sentry.hook.printer

import com.yl.lib.sentry.hook.util.PrivacyLog

/**
 * @author yulun
 * @sinice 2021-09-24 15:46
 */
class DefaultLogPrint : BasePrinter() {
    override fun print(msg: String) {
        PrivacyLog.i("msg : $msg")
    }

    override fun print(funName: String, funAlias: String, msg: String) {
        PrivacyLog.i("funName : $funName , funAlias : $funAlias , msg : $msg")
    }
}