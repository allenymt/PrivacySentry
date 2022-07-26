package com.yl.lib.sentry.hook

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.yl.lib.sentry.hook.printer.BaseFilePrinter
import com.yl.lib.sentry.hook.printer.BasePrinter
import com.yl.lib.sentry.hook.printer.DefaultFilePrint
import com.yl.lib.sentry.hook.printer.PrintCallBack
import com.yl.lib.sentry.hook.util.PrivacyLog
import com.yl.lib.sentry.hook.util.PrivacyUtil
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author yulun
 * @sinice 2021-09-24 14:33
 */
class PrivacySentry {
    object Privacy {
        private var mBuilder: PrivacySentryBuilder? = PrivacySentryBuilder()
        private val bInit = AtomicBoolean(false)
        private val bFilePrintFinish = AtomicBoolean(false)
        var bShowPrivacy = false
        private var ctx: Application? = null

        /**
         *  transform简单初始化，需要搭配插件使用
         */
        fun initTransform(ctx: Application) {
            var builder = PrivacySentryBuilder()
            init(ctx, builder)
        }

        /**
         *  完整版初始化
         */
        fun init(
            ctx: Application, builder: PrivacySentryBuilder?
        ) {
            if (bInit.compareAndSet(false, true)) {
                mBuilder = builder
                initInner(ctx)
            }
        }

        private fun initInner(ctx: Application) {
            PrivacyLog.i("call initInner")
            this.ctx = ctx
            mBuilder?.getWatchTime()?.let {
                PrivacyLog.i("delay stop watch $it")
                var handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    stop()
                }, it)
            }

            mBuilder?.addPrinter(defaultFilePrinter(ctx, mBuilder))
        }

        /**
         * 停止文件写入
         */
        fun stop() {
            if (bFilePrintFinish.compareAndSet(false, true)) {
                bFilePrintFinish.set(true)
                PrivacyLog.i("call stopWatch")
                mBuilder?.getPrinterList()?.filterIsInstance<BaseFilePrinter>()?.forEach {
                    // 强制写入文件
                    it.flushToFile()
                    // 结果回调
                    mBuilder?.getResultCallBack()?.onResultCallBack(it.resultFileName)
                }
            }
        }

        /**
         * 记录展示隐私协议，调用时机一般为 隐私协议点击确定的时候，必须调用
         */
        fun updatePrivacyShow() {
            if (bShowPrivacy) {
                return
            }
            PrivacyLog.i("call updatePrivacyShow")
            bShowPrivacy = true
            mBuilder?.getPrinterList()?.filterIsInstance<BaseFilePrinter>()
                ?.forEach { it.appendData("点击隐私协议确认", "点击隐私协议确认", "点击隐私协议确认") }
        }

        fun hasShowPrivacy(): Boolean {
            return bShowPrivacy
        }

        fun isDebug(): Boolean {
            return mBuilder?.debug ?: true
        }

        fun getContext(): Application? {
            return ctx ?: null
        }

        fun getBuilder(): PrivacySentryBuilder? {
            return mBuilder ?: null
        }

        /**
         * 当前写入文件任务是否结束
         * @return Boolean
         */
        fun isFilePrintFinish(): Boolean {
            return bFilePrintFinish.get()
        }

        /**
         * 关闭游客模式
         */
        fun closeVisitorModel() {
            mBuilder?.configVisitorModel(false)
        }

        /**
         * 打开游客模式
         */
        fun openVisitorModel() {
            mBuilder?.configVisitorModel(true)
        }

        private fun defaultFilePrinter(
            ctx: Context,
            builder: PrivacySentryBuilder?
        ): List<BasePrinter> {
            var fileName = builder?.getResultFileName() ?: "privacy_result_${
                PrivacyUtil.Util.formatTime(
                    System.currentTimeMillis()
                )
            }"
            PrivacyLog.i("print fileName is $fileName")
            return listOf(
                DefaultFilePrint(
                    "${ctx.getExternalFilesDir(null)}${File.separator}privacy${File.separator}$fileName.xls",
                    printCallBack = object : PrintCallBack {
                        override fun checkPrivacyShow(): Boolean {
                            return hasShowPrivacy()
                        }

                        override fun stopWatch() {
                            PrivacyLog.i("stopWatch")
                            Privacy.stop()
                        }
                    }, watchTime = builder?.getWatchTime()
                )
            )
        }

    }
}

/**
 * 检测结果回调，业务方自行处理
 */
public interface PrivacyResultCallBack {
    fun onResultCallBack(filePath: String)
}
