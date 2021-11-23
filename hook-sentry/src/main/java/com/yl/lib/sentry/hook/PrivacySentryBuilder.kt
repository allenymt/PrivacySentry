package com.yl.lib.sentry.hook

import com.yl.lib.sentry.hook.hook.BaseHooker
import com.yl.lib.sentry.hook.printer.BasePrinter

/**
 * @author yulun
 * @sinice 2021-09-24 15:07
 */
class PrivacySentryBuilder {

    var debug: Boolean = true

    private var hookList: ArrayList<BaseHooker>? = null

    private var mPrinterList: ArrayList<BasePrinter>? = null

    private var watchTime: Long? = null

    constructor() {
        hookList = ArrayList()
    }

    fun getPrinterList(): ArrayList<BasePrinter>? {
        return mPrinterList
    }

    fun getHookerList(): ArrayList<BaseHooker>? {
        return hookList
    }

    fun getWatchTime(): Long? {
        return watchTime
    }

    fun addPrinter(basePrinter: BasePrinter): PrivacySentryBuilder {
        if (mPrinterList == null) {
            mPrinterList = ArrayList()
        }
        mPrinterList?.add(basePrinter)
        return this
    }

    fun addPrinter(basePrinter: List<BasePrinter>): PrivacySentryBuilder {
        if (mPrinterList == null) {
            mPrinterList = ArrayList()
        }
        mPrinterList?.addAll(basePrinter)
        return this
    }

    fun syncDebug(debug: Boolean): PrivacySentryBuilder {
        this.debug = debug
        return this
    }

    fun configHook(baseHooker: BaseHooker): PrivacySentryBuilder {
        hookList?.add(baseHooker!!)
        return this
    }

    fun configWatchTime(watchTime: Long): PrivacySentryBuilder {
        this.watchTime = watchTime
        return this
    }
}