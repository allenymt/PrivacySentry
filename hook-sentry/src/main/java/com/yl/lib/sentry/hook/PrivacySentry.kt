package com.yl.lib.sentry.hook

import com.yl.lib.sentry.hook.hook.ams.AmsHooker
import com.yl.lib.sentry.hook.hook.BaseHookBuilder
import com.yl.lib.sentry.hook.hook.cms.CmsHooker
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

        fun init(){
            init(null)
        }

        fun init(builder: PrivacySentryBuilder?) {
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
                initInner()
            }

        }

        private fun initInner() {
            mBuilder?.getAmsHookBuilder()?.let {
                AmsHooker(it).hook()
            }

            mBuilder?.getCmsHookBuilder()?.let {
                CmsHooker(it).hook()
            }
        }

        fun isDebug(): Boolean {
            return mBuilder?.debug!!
        }


        fun defaultPrinter(): List<BasePrinter> {
            return listOf(DefaultLogPrint())
        }

        fun defaultAmsHookBuilder(mBuilder: PrivacySentryBuilder): BaseHookBuilder {
            var amsMethod = ArrayList<String>()
            amsMethod.add("checkPermission")
            amsMethod.add("getRunningTasks")
            amsMethod.add("getRunningAppProcesses")
            return BaseHookBuilder("ams", amsMethod, mBuilder.getPrinterList())
        }

        fun defaultTmsHookBuilder(mBuilder: PrivacySentryBuilder): BaseHookBuilder {
            var tmsMethod = ArrayList<String>()
            return BaseHookBuilder("tms", tmsMethod, mBuilder.getPrinterList())
        }

        fun defaultPmsHookBuilder(mBuilder: PrivacySentryBuilder): BaseHookBuilder {
            var pmsMethod = ArrayList<String>()
            return BaseHookBuilder("pms", pmsMethod, mBuilder.getPrinterList())
        }

        fun defaultCmsHookBuilder(mBuilder: PrivacySentryBuilder): BaseHookBuilder {
            var cmsMethod = ArrayList<String>()
            cmsMethod.add("getPrimaryClip")
            return BaseHookBuilder("cms", cmsMethod, mBuilder.getPrinterList())
        }
    }
}