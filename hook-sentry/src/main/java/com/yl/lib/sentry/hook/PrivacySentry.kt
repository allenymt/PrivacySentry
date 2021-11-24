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
import com.yl.lib.sentry.hook.printer.*
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
        private const val defaultWatchTime: Long = 55 * 1000
        var bShowPrivacy = false
        private var ctx: Application? = null

        fun init(ctx: Application) {
            init(null, ctx)
        }

        fun init(builder: PrivacySentryBuilder?, ctx: Application) {
            if (bInit.compareAndSet(false, true)) {
                if (builder == null) {
                    mBuilder = PrivacySentryBuilder()
                    mBuilder?.addPrinter(defaultPrinter(ctx))
                    mBuilder?.configHook(defaultAmsHook(mBuilder!!))
                        ?.configHook(defaultPmsHook(mBuilder!!))
                        ?.configHook(defaultTmsHook(mBuilder!!))
                        ?.configHook(defaultCmsHook(mBuilder!!))
                        ?.configWatchTime(defaultWatchTime)
                        ?.syncDebug(true)
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
                var handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    stopWatch()
                }, it)
            }
        }

        fun stopWatch() {
            PrivacyLog.i("call stopWatch")
            mBuilder?.getHookerList()?.forEach {
                it.reduction(ctx!!)
            }
            mBuilder?.getPrinterList()?.filterIsInstance<BaseWatchPrinter>()?.forEach { it.flush() }
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
            return mBuilder?.debug ?: false
        }

        fun defaultPrinter(ctx: Context): List<BasePrinter> {
            return listOf(
                DefaultLogPrint(), DefaultFilePrint(
                    "${ctx.externalCacheDir}${File.separator}privacy_result_${
                        PrivacyUtil.Util.formatTime(
                            System.currentTimeMillis()
                        )
                    }.xls",
                    printCallBack = object : PrintCallBack {
                        override fun checkPrivacyShow(): Boolean {
                            return hasShowPrivacy()
                        }

                        override fun stopWatch() {
                            PrivacyLog.i("stopWatch")
                            stopWatch()
                        }
                    }, ctx = ctx
                )
            )
        }

        fun defaultAmsHook(mBuilder: PrivacySentryBuilder): BaseHooker {
            var amsMethod = HashMap<String, String>()
//            amsMethod["checkPermission"] = "checkPermission"
            amsMethod["getRunningTasks"] = "getRunningTasks"
            amsMethod["getRunningAppProcesses"] = "getRunningAppProcesses"
            return AmsHooker(BaseHookBuilder("ams", amsMethod, mBuilder.getPrinterList()))
        }

        fun defaultTmsHook(mBuilder: PrivacySentryBuilder): BaseHooker {
            var tmsMethod = HashMap<String, String>()

            // getDeviceId
            tmsMethod["getDeviceIdWithFeature"] = "getDeviceId" // 11
            tmsMethod["getDeviceId"] = "getDeviceId" // 10 9

            // getImei
            tmsMethod["getImeiForSlot"] = "getImei" // 9 10 11

            // getIMSI
            tmsMethod["getSubscriberIdForSubscriber"] = "getIMSI" // 9

            // getSimSerialNumber
            tmsMethod["getIccSerialNumberForSubscriber"] = "getSimSerialNumber"
            return TmsHooker(BaseHookBuilder("tms", tmsMethod, mBuilder.getPrinterList()))
        }

        fun defaultPmsHook(mBuilder: PrivacySentryBuilder): BaseHooker {
            var pmsMethod = HashMap<String, String>()
            pmsMethod.put("getInstalledPackages", "getInstalledPackages")
            return PmsHooker(BaseHookBuilder("pms", pmsMethod, mBuilder.getPrinterList()))
        }

        fun defaultCmsHook(mBuilder: PrivacySentryBuilder): BaseHooker {
            var cmsMethod = HashMap<String, String>()
            cmsMethod.put("getPrimaryClip", "getPrimaryClip")
            return CmsHooker(BaseHookBuilder("cms", cmsMethod, mBuilder.getPrinterList()))
        }
    }
}