package com.yl.lib.sentry.hook.printer

import com.yl.lib.sentry.hook.util.PrivacyLog

/**
 * @author yulun
 * @sinice 2021-11-22 14:26
 */
abstract class BaseWatchPrinter : BasePrinter {

    val printCallBack: PrintCallBack

    constructor(printCallBack: PrintCallBack) {
        this.printCallBack = printCallBack
    }

    final override fun print(funName: String, funAlias: String, msg: String) {
        if (!printCallBack.checkPrivacyShow()) {
            PrivacyLog.e("check!!! 还未展示隐私协议，Illegal print")
        }
        appendData(funName, funAlias, msg)
    }

    abstract fun flush()

    abstract fun appendData(funName: String, funAlias: String, msg: String)
}