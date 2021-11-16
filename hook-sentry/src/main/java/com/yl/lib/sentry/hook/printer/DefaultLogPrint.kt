package com.yl.lib.sentry.hook.printer

import com.yl.lib.sentry.hook.util.PrivacyLog
/**
 * @author yulun
 * @sinice 2021-09-24 15:46
 */
class DefaultLogPrint : BasePrinter() {

    override fun print(name: String, msg: String) {
        PrivacyLog.i("name is $name, msg is $msg")
    }
}