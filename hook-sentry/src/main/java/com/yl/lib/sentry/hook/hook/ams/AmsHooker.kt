package com.yl.lib.sentry.hook.hook.ams

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import com.yl.lib.sentry.hook.hook.BaseHookBuilder
import com.yl.lib.sentry.hook.hook.BaseHooker
import java.lang.reflect.Proxy

/**
 * @author yulun
 * @sinice 2021-09-24 15:32
 */
class AmsHooker(baseHookerHookBuilder: BaseHookBuilder?) : BaseHooker(baseHookerHookBuilder) {
    var originAms: Any? = null
    var defaultSingleton: Any? = null
    override fun hook(ctx: Application) {
        if (!legalAndroidVersion()) {
            return
        }
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val clazz: Class<*> = ActivityManager::class.java
                val singletonIAMS = clazz.getDeclaredField("IActivityManagerSingleton")
                singletonIAMS.isAccessible = true
                defaultSingleton = singletonIAMS[null]
            } else {
                val activityManagerNativeClazz = Class.forName("android.app.ActivityManagerNative")
                val gDefaultField = activityManagerNativeClazz.getDeclaredField("gDefault")
                gDefaultField.isAccessible = true
                defaultSingleton = gDefaultField[null]
            }
            val singletonClazz = Class.forName("android.util.Singleton")
            val mInstance = singletonClazz.getDeclaredField("mInstance")
            mInstance.isAccessible = true
            originAms = mInstance[defaultSingleton]
            val iAmClazz = Class.forName("android.app.IActivityManager")
            // 前面搞了那么久就是要拿到原始的IActivityManager binder在当前进程的proxy对象

            var amsProxy = Proxy.newProxyInstance(
                Thread.currentThread().contextClassLoader,
                arrayOf(iAmClazz),
                baseHookerHookBuilder?.let {
                    AMSProxy(
                        originAms,
                        it
                    )
                }
            )
            mInstance[defaultSingleton] = amsProxy
            if (amsProxy === mInstance[defaultSingleton]) {
                baseHookerHookBuilder?.doPrinter("hookSystemServices ams succeed : ${amsProxy?.javaClass?.name}")
            }
        } catch (e: Exception) {
            baseHookerHookBuilder?.doPrinter("hookSystemServices ams failed")
        }
    }

    override fun reduction(ctx: Application) {
        if (!legalAndroidVersion()) {
            return
        }
        try {
            val singletonClazz = Class.forName("android.util.Singleton")
            val mInstance = singletonClazz.getDeclaredField("mInstance")
            mInstance.isAccessible = true
            mInstance[defaultSingleton] = originAms
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}