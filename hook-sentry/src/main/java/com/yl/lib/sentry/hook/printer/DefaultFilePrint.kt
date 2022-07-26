package com.yl.lib.sentry.hook.printer

import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.excel.ExcelBuildDataListener
import com.yl.lib.sentry.hook.excel.ExcelUtil
import com.yl.lib.sentry.hook.util.PrivacyLog
import com.yl.lib.sentry.hook.watcher.DelayTimeWatcher

/**
 * @author yulun
 * @sinice 2021-09-24 15:47
 * 为了可以更直观的查看统计结果，默认采用excel文件形式输出
 */
class DefaultFilePrint : BaseFilePrinter {

    // 隐私函数调用 堆栈跟踪
    private val titlePrivacyLegal = arrayOf("调用时间(倒序排序)", "别名", "函数名", "调用堆栈")
    private val sheetPrivacyLegal = 0

    // 隐私函数调用次数聚合
    private val titlePrivacyCount = arrayOf("别名", "函数名", "调用堆栈", "调用次数")
    private val sheetPrivacyCount = 1

    private var hasInit = false
    private var privacyFunBeanList: ArrayList<PrivacyFunBean> = ArrayList()

    constructor(
        fileName: String,
        printCallBack: PrintCallBack,
        watchTime: Long?
    ) : super(printCallBack, fileName) {
        PrivacyLog.i("file name is $fileName")
        ExcelUtil.instance.checkDelOldFile(fileName)
        DelayTimeWatcher(watchTime ?: 60 * 60 * 1000, Runnable {
            flushToFile()
        }).start()
    }

    override fun logPrint(msg: String) {
    }

    override fun flushToFile() {
        assert(resultFileName != null)

        if (PrivacySentry.Privacy.getBuilder()?.isEnableFileResult() == false) {
            return
        }
        if (privacyFunBeanList.isEmpty())
            return
        if (!hasInit) {
            hasInit = true
            ExcelUtil.instance.initExcel(
                resultFileName,
                arrayListOf("隐私合规", "调用次数"),
                arrayListOf(titlePrivacyLegal, titlePrivacyCount),
                arrayListOf(sheetPrivacyLegal, sheetPrivacyCount)
            )
        }
        var newFunBeanList = ArrayList<PrivacyFunBean>()
        newFunBeanList.addAll(privacyFunBeanList)
        flushSheetPrivacyLegal(newFunBeanList)
        flushSheetPrivacyCount(newFunBeanList)
        newFunBeanList.clear()
    }

    override fun appendData(funName: String, funAlias: String, msg: String) {
        if (funName == null || funAlias == null)
            return
        privacyFunBeanList.add(PrivacyFunBean(funAlias, funName, msg, 1))
    }

    private fun flushSheetPrivacyCount(funBeanList: ArrayList<PrivacyFunBean>) {
        try {
            var privacyFunBeanMap: HashMap<String, PrivacyFunBean> = HashMap()
            PrivacyLog.e("call flushSheetPrivacyCount")
            funBeanList.filter { !it.funName.equals("点击隐私协议确认") }.forEach {
                if (privacyFunBeanMap[it.buildStackTrace()] == null) {
                    privacyFunBeanMap[it.buildStackTrace()] = it
                } else {
                    privacyFunBeanMap[it.buildStackTrace()]?.addSelf()
                }
            }
            ExcelUtil.instance.writeObjListToExcel(
                privacyFunBeanMap?.map { it.value },
                resultFileName,
                sheetPrivacyCount, object : ExcelBuildDataListener {
                    override fun buildData(
                        sheetIndex: Int,
                        privacyFunBean: PrivacyFunBean
                    ): List<String> {
                        return listOf(
                            privacyFunBean.funAlias.toString(),
                            privacyFunBean.funName.toString(),
                            privacyFunBean.buildStackTrace(),
                            privacyFunBean.count.toString()
                        )
                    }
                }
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    private fun flushSheetPrivacyLegal(funBeanList: ArrayList<PrivacyFunBean>) {
        try {
            PrivacyLog.e("call flushSheetPrivacyLegal")
            ExcelUtil.instance.writeObjListToExcel(
                funBeanList,
                resultFileName,
                sheetPrivacyLegal,
                object : ExcelBuildDataListener {
                    override fun buildData(
                        sheetIndex: Int,
                        privacyFunBean: PrivacyFunBean
                    ): List<String> {
                        return listOf(
                            privacyFunBean.appendTime.toString(),
                            privacyFunBean.funAlias.toString(),
                            privacyFunBean.funName.toString(),
                            privacyFunBean.buildStackTrace()
                        )
                    }

                })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}
