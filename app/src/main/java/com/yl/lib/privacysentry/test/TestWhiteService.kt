package com.yl.lib.privacysentry.test

import android.app.Service
import android.content.Intent
import android.os.IBinder

class TestWhiteService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}