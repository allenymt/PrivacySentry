package com.yl.lib.privacysentry

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.multidex.MultiDex
import com.yl.lib.privacysentry.test.PrivacyMethod
import com.yl.lib.sentry.hook.PrivacyResultCallBack
import com.yl.lib.sentry.hook.PrivacySentry
import com.yl.lib.sentry.hook.PrivacySentryBuilder
import com.yl.lib.sentry.hook.util.PrivacyLog

/**
 * @author yulun
 * @sinice 2021-11-19 10:20
 */
class APP : Application() {
    var testAppSerial = Build.SERIAL
    override fun onCreate() {
        super.onCreate()
        var str = testJustSerial()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this);

        Thread { initPrivacyTransformComplete() }.start()

    }

    private fun initPrivacyTransform() {
        PrivacySentry.Privacy.initTransform(this)
    }


    private fun initPrivacyTransformComplete() {

        // 完整版配置
        var builder = PrivacySentryBuilder()
            // 自定义文件结果的输出名
            .configResultFileName("demo_test")
            //自定义检测时间，也支持主动停止检测 PrivacySentry.Privacy.stopWatch()
            .configWatchTime(10 * 60 * 1000)
            // 打开写入文件开关
            .enableFileResult(true)
            // 打开游客模式
            .configVisitorModel(true)
            // 文件输出后的回调
            .configResultCallBack(object : PrivacyResultCallBack {
                override fun onResultCallBack(filePath: String) {
                    PrivacyLog.i("result file patch is $filePath")
                }
            })
        // 添加默认结果输出，包含log输出和文件输出
        PrivacySentry.Privacy.init(this, builder)
        // 简易版配置
//        PrivacySentry.Privacy.init(this)
    }

    fun testJustSerial() {
        val serialNum = android.os.Build.SERIAL
    }
}