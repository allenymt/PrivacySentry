package com.yl.lib.sentry.hook.hook

import android.app.ActivityManager
import android.os.Build
import android.telephony.TelephonyManager


/**
 * @author yulun
 * @sinice 2021-09-24 14:50
 */
class TmsHooker(baseHookerHookBuilder: BaseHookBuilder?) : BaseHooker(baseHookerHookBuilder) {
    override fun hook() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        try {
            val clazz: Class<*> = TelephonyManager::class.java

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}