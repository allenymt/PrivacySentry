package com.yl.lib.sentry.hook.printer

import android.content.Context
import com.yl.lib.sentry.hook.util.ExcelUtil
import com.yl.lib.sentry.hook.util.PrivacyFunBean
import com.yl.lib.sentry.hook.util.PrivacyLog

/**
 * @author yulun
 * @sinice 2021-09-24 15:47
 * 为了可以更直观的查看统计结果，默认采用excel文件形式输出
 */
class DefaultFilePrint : BaseWatchPrinter {

    private var fileName: String = ""

    private val title = arrayOf("别名", "函数名", "调用堆栈", "调用次数")
    private val sheetPrivacyCount = 1
    private val sheetPrivacyLegal = 0
    private val ctx: Context

    var privacyFunBeanMap: HashMap<String, PrivacyFunBean> = HashMap()
    var privacyFunBeanList: ArrayList<PrivacyFunBean> = ArrayList()

    constructor(
        fileName: String,
        printCallBack: PrintCallBack,
        ctx: Context
    ) : super(printCallBack) {
        this.fileName = fileName
        this.ctx = ctx
        ExcelUtil.instance.initExcel(
            fileName,
            arrayListOf("隐私合规", "调用次数"),
            title,
            arrayListOf(sheetPrivacyLegal, sheetPrivacyCount)
        )
    }

    override fun print(msg: String) {
    }

    override fun flush() {
        assert(fileName != null)
        if (privacyFunBeanList.isEmpty())
            return
        flushSheetPrivacyLegal()
        flushSheetPrivacyCount()
    }

    override fun appendData(funName: String, funAlias: String, msg: String) {
        if (funName == null || funAlias == null)
            return
        privacyFunBeanList.add(PrivacyFunBean(funAlias, funName, msg, 1))
    }

    private fun flushSheetPrivacyCount() {
        try {
            PrivacyLog.e("call flushSheetPrivacyCount")
            privacyFunBeanList.filter { !it.funName.equals("点击隐私协议确认") }.forEach {
                if (privacyFunBeanMap[it.buildStackTrace()] == null) {
                    privacyFunBeanMap[it.buildStackTrace()] = it
                } else {
                    privacyFunBeanMap[it.buildStackTrace()]?.addSelf()
                }
            }
            ExcelUtil.instance.writeObjListToExcel(
                privacyFunBeanMap?.map { it.value },
                fileName,
                sheetPrivacyCount
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    private fun flushSheetPrivacyLegal() {
        try {
            PrivacyLog.e("call flushSheetPrivacyLegal")
            ExcelUtil.instance.writeObjListToExcel(privacyFunBeanList, fileName, sheetPrivacyLegal)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}