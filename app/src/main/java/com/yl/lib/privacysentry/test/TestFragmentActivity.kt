package com.yl.lib.privacysentry.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yl.lib.privacysentry.R
import com.yl.lib.privacysentry.test.ui.main.TestPermissionFragment

class TestFragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_frament)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TestPermissionFragment.newInstance())
                .commitNow()
        }
    }
}