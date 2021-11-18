package com.yl.lib.sentry.hook

import android.app.Application
import android.content.Context
import com.yl.lib.sentry.hook.hook.BaseHookBuilder
import com.yl.lib.sentry.hook.hook.ams.AmsHooker
import com.yl.lib.sentry.hook.hook.cms.CmsHooker
import com.yl.lib.sentry.hook.hook.pms.PmsHooker
import com.yl.lib.sentry.hook.hook.tms.TmsHooker
import com.yl.lib.sentry.hook.printer.BasePrinter
import com.yl.lib.sentry.hook.printer.DefaultLogPrint
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author yulun
 * @sinice 2021-09-24 14:33
 */
class PrivacySentry {
    object Privacy {
        private var mBuilder: PrivacySentryBuilder? = null
        private val bInit = AtomicBoolean(false)
        var bShowPrivacy = false

        fun init(ctx: Application) {
            init(null,ctx)
        }

        fun init(builder: PrivacySentryBuilder?,ctx: Context) {
            if (bInit.compareAndSet(false, true)) {
                if (builder == null) {
                    mBuilder = PrivacySentryBuilder()
                    mBuilder?.addPrinter(defaultPrinter())
                    mBuilder?.configAmsHook(defaultAmsHookBuilder(mBuilder!!))
                        ?.configPmsHook(defaultPmsHookBuilder(mBuilder!!))
                        ?.configTmsHook(defaultTmsHookBuilder(mBuilder!!))
                        ?.configCmsHook(defaultCmsHookBuilder(mBuilder!!))
                        ?.syncDebug(true)
                } else {
                    mBuilder = builder
                }
                initInner(ctx)
            }

        }

        private fun initInner(ctx: Context) {
            mBuilder?.getAmsHookBuilder()?.let {
                AmsHooker(it).hook(ctx)
            }

            mBuilder?.getCmsHookBuilder()?.let {
                CmsHooker(it).hook(ctx)
            }

            mBuilder?.getTmsHookBuilder()?.let {
                TmsHooker(it).hook(ctx)
            }

            mBuilder?.getPmsHookBuilder()?.let {
                PmsHooker(it).hook(ctx)
            }
        }

        /**
         * 记录展示隐私协议，调用时机一般为 隐私协议点击关闭的时候，必须调用
         */
        fun updatePrivacyShow() {
            bShowPrivacy = true
        }


        fun isDebug(): Boolean {
            return mBuilder?.debug!!
        }


        fun defaultPrinter(): List<BasePrinter> {
            return listOf(DefaultLogPrint())
        }

        fun defaultAmsHookBuilder(mBuilder: PrivacySentryBuilder): BaseHookBuilder {
            var amsMethod = HashMap<String, String>()
            amsMethod["checkPermission"] = "checkPermission"
            amsMethod["getRunningTasks"] = "getRunningTasks"
            amsMethod["getRunningAppProcesses"] = "getRunningAppProcesses"
            return BaseHookBuilder("ams", amsMethod, mBuilder.getPrinterList())
        }

        fun defaultTmsHookBuilder(mBuilder: PrivacySentryBuilder): BaseHookBuilder {
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
            return BaseHookBuilder("tms", tmsMethod, mBuilder.getPrinterList())
        }

        fun defaultPmsHookBuilder(mBuilder: PrivacySentryBuilder): BaseHookBuilder {
            var pmsMethod = HashMap<String, String>()
            pmsMethod.put("getInstalledPackages","getInstalledPackages")
            return BaseHookBuilder("pms", pmsMethod, mBuilder.getPrinterList())
        }

        fun defaultCmsHookBuilder(mBuilder: PrivacySentryBuilder): BaseHookBuilder {
            var cmsMethod = HashMap<String, String>()
            cmsMethod.put("getPrimaryClip", "getPrimaryClip")
            return BaseHookBuilder("cms", cmsMethod, mBuilder.getPrinterList())
        }
    }
}