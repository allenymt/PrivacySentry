package com.yl.lib.privacysentry.process

import android.app.Application
import android.app.IntentService
import android.content.Intent
import android.os.Build
import com.yl.lib.privacysentry.test.PrivacyMethod
import com.yl.lib.sentry.hook.util.MainProcessUtil
import com.yl.lib.sentry.hook.util.PrivacyLog

/**
 * @author yulun
 * @sinice 2021-07-06 15:47
 */
class MultiProcessB : IntentService("MultiProcessB") {

    override fun onHandleIntent(intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PrivacyLog.i("mutliProcss " + Application.getProcessName())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PrivacyMethod.PrivacyMethod.getIMEI(context = this)
        }
        PrivacyMethod.PrivacyMethod.isInstalled(
            context = this,
            pkgName = MainProcessUtil.MainProcessChecker.getProcessName(this)
        )
    }
}