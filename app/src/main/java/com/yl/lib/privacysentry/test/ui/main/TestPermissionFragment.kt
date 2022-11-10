package com.yl.lib.privacysentry.test.ui.main

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.yl.lib.privacysentry.R
import com.yl.lib.sentry.hook.util.PrivacyLog
import com.yl.lib.sentry.hook.util.PrivacyUtil

class TestPermissionFragment : Fragment() {

    companion object {
        fun newInstance() = TestPermissionFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestMultiplePermissions = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            Thread.currentThread().stackTrace
            permissions.entries.forEach {

                PrivacyLog.i("TestFragmentFragment ${PrivacyUtil.Util.getStackTrace()} \n ${it.key} = ${it.value}")
            }
        }

        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.CAMERA
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        requestPermissions(arrayOf(
            Manifest.permission.BODY_SENSORS
        ),10000)
    }
}