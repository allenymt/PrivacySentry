package com.yl.lib.privacysentry.test

/**
 * @author yulun
 * @since 2025-05-14 10:07
 */
import android.app.Service
import android.content.Intent
import android.os.IBinder

class TestService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}
