package com.yl.lib.sentry.hook

import com.yl.lib.sentry.hook.hook.BaseHookBuilder
import com.yl.lib.sentry.hook.printer.BasePrinter

/**
 * @author yulun
 * @sinice 2021-09-24 15:07
 */
class PrivacySentryBuilder {

    var debug: Boolean = true

    private var amsHookBuilder: BaseHookBuilder? = null

    private var pmsHookBuilder: BaseHookBuilder? = null

    private var tmsHookBuilder: BaseHookBuilder? = null

    private var cmsHookBuilder: BaseHookBuilder? = null

    private var mPrinterList: ArrayList<BasePrinter>? = null

    fun getPrinterList(): ArrayList<BasePrinter>? {
        return mPrinterList
    }

    fun getAmsHookBuilder(): BaseHookBuilder? {
        return amsHookBuilder
    }

    fun getPmsHookBuilder(): BaseHookBuilder? {
        return pmsHookBuilder
    }

    fun getTmsHookBuilder(): BaseHookBuilder? {
        return tmsHookBuilder
    }

    fun getCmsHookBuilder(): BaseHookBuilder? {
        return cmsHookBuilder
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

    fun configAmsHook(amsHookHookBuilder: BaseHookBuilder): PrivacySentryBuilder {
        this.amsHookBuilder = amsHookHookBuilder
        return this
    }

    fun configPmsHook(pmsHookHookBuilder: BaseHookBuilder): PrivacySentryBuilder {
        this.pmsHookBuilder = pmsHookHookBuilder
        return this
    }

    fun configTmsHook(tmsHookHookBuilder: BaseHookBuilder): PrivacySentryBuilder {
        this.tmsHookBuilder = tmsHookHookBuilder
        return this
    }

    fun configCmsHook(cmsHookHookBuilder: BaseHookBuilder): PrivacySentryBuilder {
        this.cmsHookBuilder = cmsHookHookBuilder
        return this
    }
}