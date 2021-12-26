package com.yl.lib.sentry.hook.printer

import com.yl.lib.sentry.hook.util.PrivacyLog

/**
 * @author yulun
 * @sinice 2021-11-22 14:26
 */
abstract class BaseFilePrinter : BasePrinter {

    val printCallBack: PrintCallBack
    val resultFileName:String

    constructor(printCallBack: PrintCallBack,resultFileName:String) {
        this.printCallBack = printCallBack
        this.resultFileName = resultFileName
    }

    final override fun filePrint(funName: String, funAlias: String, msg: String) {
        if (!printCallBack.checkPrivacyShow()) {
            PrivacyLog.e("check!!! 还未展示隐私协议，Illegal print")
        }
        appendData(funName, funAlias, msg)
    }

    abstract fun flushToFile()

    abstract fun appendData(funName: String, funAlias: String, msg: String)
}