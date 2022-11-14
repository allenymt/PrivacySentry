package com.yl.lib.sentry.hook

import com.yl.lib.sentry.hook.printer.BasePrinter
import com.yl.lib.sentry.hook.printer.DefaultLogPrint
import com.yl.lib.sentry.hook.util.MainProcessUtil

/**
 * @author yulun
 * @sinice 2021-09-24 15:07
 */
class PrivacySentryBuilder {

    var debug: Boolean = true

    //日志输出 和 文件输出
    private var mPrinterList: ArrayList<BasePrinter>? = null

    // 默认的监听时间
    private var watchTime: Long = 3 * 60 * 1000

    // 结束回调
    private var privacyResultCallBack: PrivacyResultCallBack? = null

    // 输出的文件名
    private var resultFileName: String? = null

    // 是否激活输入日志到文件
    private var enableFileResult: Boolean = true

    // 游客模式，拦截所有敏感方法
    @Volatile
    private var visitorModel: Boolean = true

    // 可以拦截读取系统剪贴板
    private var enableReadClipBoard: Boolean = true

    constructor() {
        addPrinter(DefaultLogPrint())
    }

    fun getPrinterList(): ArrayList<BasePrinter>? {
        return mPrinterList
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

    fun configVisitorModel(visitorModel: Boolean): PrivacySentryBuilder {
        this.visitorModel = visitorModel
        return this
    }

    fun isVisitorModel(): Boolean {
        return visitorModel
    }

    fun enableFileResult(enableFileResult: Boolean): PrivacySentryBuilder {
        this.enableFileResult = enableFileResult
        return this
    }

    fun isEnableFileResult(): Boolean {
        return enableFileResult
    }

    fun enableReadClipBoard(enableReadClipBoard: Boolean): PrivacySentryBuilder {
        this.enableReadClipBoard = enableReadClipBoard
        return this
    }

    fun isEnableReadClipBoard(): Boolean {
        return enableReadClipBoard
    }

}
