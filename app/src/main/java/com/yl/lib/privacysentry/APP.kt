package com.yl.lib.privacysentry

import android.app.Application
import com.yl.lib.sentry.hook.PrivacySentry

/**
 * @author yulun
 * @sinice 2021-11-19 10:20
 */
class APP : Application() {
    override fun onCreate() {
        super.onCreate()
        PrivacySentry.Privacy.init(this)
    }
}