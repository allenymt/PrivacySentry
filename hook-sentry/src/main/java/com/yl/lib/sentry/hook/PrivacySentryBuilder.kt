package com.yl.lib.sentry.hook

import com.yl.lib.sentry.hook.hook.BaseHooker
import com.yl.lib.sentry.hook.printer.BasePrinter
import com.yl.lib.sentry.hook.printer.DefaultLogPrint
import com.yl.lib.sentry.hook.util.MainProcessUtil

/**
 * @author yulun
 * @sinice 2021-09-24 15:07
 */
class PrivacySentryBuilder {

    var debug: Boolean = true

    private var hookList: ArrayList<BaseHooker>? = null
    private var mPrinterList: ArrayList<BasePrinter>? = null
    private var watchTime: Long = 3 * 60 * 1000
    private var privacyResultCallBack: PrivacyResultCallBack? = null
    private var resultFileName: String? = null
    private var privacyType: PrivacyType = PrivacyType.RUNTIME

    constructor() {
        hookList = ArrayList()
        addPrinter(DefaultLogPrint())
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

    fun getResultCallBack(): PrivacyResultCallBack? {
        return privacyResultCallBack
    }

    fun getResultFileName(): String? {
        // 这里可能是多进程
        return if (MainProcessUtil.MainProcessChecker.isMainProcess(PrivacySentry.Privacy.getContext())) {
            resultFileName
        } else {
            var processName = PrivacySentry.Privacy.getContext()?.let {
                MainProcessUtil.MainProcessChecker.getProcessName(PrivacySentry.Privacy.getContext()!!)
            } ?: ""
            "${processName}_$resultFileName"
        }
    }

    fun getPrivacyType(): PrivacyType {
        return privacyType
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

    fun configResultCallBack(privacyResultCallBack: PrivacyResultCallBack?): PrivacySentryBuilder {
        this.privacyResultCallBack = privacyResultCallBack
        return this
    }

    fun configResultFileName(resultFileName: String): PrivacySentryBuilder {
        this.resultFileName = resultFileName
        return this
    }

    fun configPrivacyType(privacyType: PrivacyType): PrivacySentryBuilder {
        privacyType?.let {
            this.privacyType = privacyType
        }
        return this
    }

    enum class PrivacyType {
        RUNTIME,
        TRANSFORM
    }
}
