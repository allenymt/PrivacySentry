package com.yl.lib.privacysentry.test

import android.app.Service
import android.content.Intent
import android.os.IBinder

class TestService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}