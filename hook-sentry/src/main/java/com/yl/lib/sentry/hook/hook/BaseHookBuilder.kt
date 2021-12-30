package com.yl.lib.sentry.hook.hook

import com.yl.lib.sentry.base.HookMethodItem
import com.yl.lib.sentry.hook.printer.BasePrinter
import com.yl.lib.sentry.hook.printer.DefaultLogPrint

/**
 * @author yulun
 * @sinice 2021-09-24 14:51
 * https://juejin.cn/post/6844903985258692621 locationManager
 */
class BaseHookBuilder {
    val name: String

    var mPrinterList: ArrayList<BasePrinter>? = null


    constructor(
        name: String,
        printerList: ArrayList<BasePrinter>?
    ) {
        this.name = name
        this.mPrinterList = printerList
    }

    fun doLogPrinter(msg: String) {
        mPrinterList?.find { it is DefaultLogPrint }?.logPrint(msg)
    }

    fun doFilePrinter(hookMethodItem: HookMethodItem, msg: String) {
        mPrinterList?.forEach {
            it.filePrint(hookMethodItem.methodName, hookMethodItem.methodDesc, "$name-$msg")
        }
    }
}