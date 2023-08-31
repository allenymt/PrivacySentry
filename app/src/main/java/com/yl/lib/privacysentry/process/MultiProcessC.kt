package com.yl.lib.privacysentry.process

import android.app.Application
import android.app.IntentService
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.yl.lib.privacysentry.test.PrivacyMethod
import com.yl.lib.sentry.hook.util.MainProcessUtil
import com.yl.lib.sentry.hook.util.PrivacyLog

/**
 * @author yulun
 * @sinice 2021-07-06 15:47
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MultiProcessC : JobService() {

//    override fun onHandleIntent(intent: Intent?) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            PrivacyLog.i("mutliProcss " + Application.getProcessName())
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            PrivacyMethod.PrivacyMethod.getIMEI(context = this)
//        }
//        PrivacyMethod.PrivacyMethod.isInstalled(
//            context = this,
//            pkgName = MainProcessUtil.MainProcessChecker.getProcessName(this)
//        )
//    }

    override fun onStartJob(params: JobParameters?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}