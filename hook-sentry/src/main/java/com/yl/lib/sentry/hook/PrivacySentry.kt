package com.yl.lib.sentry.hook

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.yl.lib.sentry.hook.hook.BaseHookBuilder
import com.yl.lib.sentry.hook.hook.BaseHooker
import com.yl.lib.sentry.hook.hook.ams.AmsHooker
import com.yl.lib.sentry.hook.hook.cms.CmsHooker
import com.yl.lib.sentry.hook.hook.pms.PmsHooker
import com.yl.lib.sentry.hook.hook.tms.TmsHooker
import com.yl.lib.sentry.hook.printer.BasePrinter
import com.yl.lib.sentry.hook.printer.BaseWatchPrinter
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
        private var mBuilder: PrivacySentryBuilder? = null
        private val bInit = AtomicBoolean(false)
        private val bfinish = AtomicBoolean(false)
        var bShowPrivacy = false
        private var ctx: Application? = null

        fun init(ctx: Application) {
            init(ctx, null)
        }

        /**
         *  builder 自定义配置
         */
        fun init(
            ctx: Application, builder: PrivacySentryBuilder?
        ) {
            if (bInit.compareAndSet(false, true)) {
                if (builder == null) {
                    mBuilder = PrivacySentryBuilder().addPrinter(defaultFilePrinter(ctx, mBuilder))
                    mBuilder = defaultConfigHookBuilder(mBuilder!!)
                } else {
                    mBuilder = builder
                }
                initInner(ctx)
            }
        }

        private fun initInner(ctx: Application) {
            PrivacyLog.i("call initInner")
            this.ctx = ctx
            mBuilder?.getHookerList()?.forEach {
                it.hook(ctx)
            }
            mBuilder?.getWatchTime()?.let {
                PrivacyLog.i("delay stop watch $it")
                var handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    stopWatch()
                }, it)
            }
            mBuilder?.addPrinter(defaultFilePrinter(ctx, mBuilder))
        }

        fun stopWatch() {
            if (bfinish.compareAndSet(false, true)) {
                bfinish.set(true)
                PrivacyLog.i("call stopWatch")
                mBuilder?.getHookerList()?.forEach {
                    it.reduction(ctx!!)
                }
                mBuilder?.getPrinterList()?.filterIsInstance<BaseWatchPrinter>()?.forEach {
                    it.flush()
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
            mBuilder?.getPrinterList()?.filterIsInstance<BaseWatchPrinter>()
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

        fun defaultConfigHookBuilder(builder: PrivacySentryBuilder): PrivacySentryBuilder {
            builder?.configHook(defaultAmsHook(builder!!))
                ?.configHook(defaultPmsHook(builder!!))
                ?.configHook(defaultTmsHook(builder!!))
                ?.configHook(defaultCmsHook(builder!!))
                ?.syncDebug(true)
            return builder
        }

        fun defaultFilePrinter(ctx: Context, builder: PrivacySentryBuilder?): List<BasePrinter> {
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
                            Privacy.stopWatch()
                        }
                    }, ctx = ctx
                )
            )
        }

        fun defaultAmsHook(mBuilder: PrivacySentryBuilder): BaseHooker {
            var amsMethod = HashMap<String, String>()
//            amsMethod["checkPermission"] = "checkPermission"
            amsMethod["getRunningTasks"] = "获取当前运行任务-getRunningTasks"
            amsMethod["getRunningAppProcesses"] = "获取当前运行进程-getRunningAppProcesses"
            return AmsHooker(BaseHookBuilder("ams", amsMethod, mBuilder.getPrinterList()))
        }

        fun defaultTmsHook(mBuilder: PrivacySentryBuilder): BaseHooker {
            var tmsMethod = HashMap<String, String>()

            // getDeviceId
            tmsMethod["getDeviceIdWithFeature"] = "获取设备id-getDeviceId" // 11
            tmsMethod["getDeviceId"] = "获取设备id-getDeviceId" // 10 9

            // getImei
            tmsMethod["getImeiForSlot"] = "获取IMEI-getImei" // 9 10 11

            // getIMSI
            tmsMethod["getSubscriberIdForSubscriber"] = "获取IMSI-getIMSI" // 9

            // getSimSerialNumber
            tmsMethod["getIccSerialNumberForSubscriber"] = "获取sim卡标识-getSimSerialNumber"
            return TmsHooker(BaseHookBuilder("tms", tmsMethod, mBuilder.getPrinterList()))
        }

        fun defaultPmsHook(mBuilder: PrivacySentryBuilder): BaseHooker {
            var pmsMethod = HashMap<String, String>()
            pmsMethod.put("getInstalledPackages", "获取安装包-getInstalledPackages")
            pmsMethod.put("queryIntentActivitiesAsUser", "读安装列表-queryIntentActivitiesAsUser")

            return PmsHooker(BaseHookBuilder("pms", pmsMethod, mBuilder.getPrinterList()))
        }

        fun defaultCmsHook(mBuilder: PrivacySentryBuilder): BaseHooker {
            var cmsMethod = HashMap<String, String>()
            cmsMethod.put("getPrimaryClip", "获取剪贴板内容-getPrimaryClip")
            return CmsHooker(BaseHookBuilder("cms", cmsMethod, mBuilder.getPrinterList()))
        }
    }
}

/**
 * 检测结果回调，业务方自行处理
 */
public interface PrivacyResultCallBack {
    fun onResultCallBack(filePath: String)
}