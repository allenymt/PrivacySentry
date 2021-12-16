package com.yl.lib.sentry.hook.hook.tms

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.IBinder
import com.yl.lib.sentry.hook.hook.BaseHookBuilder
import com.yl.lib.sentry.hook.hook.BaseHooker
import com.yl.lib.sentry.hook.hook.HookStubHandler
import java.lang.reflect.Proxy


/**
 * @author yulun
 * @sinice 2021-09-24 14:50
 */
class TmsHooker(baseHookerHookBuilder: BaseHookBuilder?) : BaseHooker(baseHookerHookBuilder) {
    var rawITelephonyBinder: IBinder? = null
    var rawPhoneSubBinder: IBinder? = null
    override fun hook(ctx: Application) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        try {
            var smClass = Class.forName("android.os.ServiceManager")
            var smMethod = smClass.getDeclaredMethod("getService", String::class.java)
            rawITelephonyBinder = smMethod.invoke(null, Context.TELEPHONY_SERVICE) as IBinder
            // 别问我为什么写死，源码里就是这样的
            rawPhoneSubBinder = smMethod.invoke(null, "iphonesubinfo") as IBinder
            hookITelephony(rawITelephonyBinder!!, smClass = smClass)
            hookIPhoneSubInfo(rawPhoneSubBinder!!, smClass)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun reduction(ctx: Application) {
        try {
            var smClass = Class.forName("android.os.ServiceManager")
            //放回ServiceManager中，替换掉原有的
            val cacheField = smClass.getDeclaredField("sCache")
            cacheField.isAccessible = true
            val cache = cacheField[null] as MutableMap<String, IBinder>
            cache[Context.TELEPHONY_SERVICE] = rawITelephonyBinder!!

            //放回ServiceManager中，替换掉原有的
            val cacheField1 = smClass.getDeclaredField("sCache")
            cacheField1.isAccessible = true
            val cache1 = cacheField1[null] as MutableMap<String, IBinder>
            cache1["iphonesubinfo"] = rawPhoneSubBinder!!
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    // getDeviceId
    // getImei
    private fun hookITelephony(rawITelephonyBinder: IBinder, smClass: Class<*>) {
        val hookedBinder = Proxy.newProxyInstance(
            smClass.classLoader, arrayOf<Class<*>>(IBinder::class.java),
            HookStubHandler(
                rawITelephonyBinder,
                baseHookerHookBuilder,
                Class.forName("com.android.internal.telephony.ITelephony"),
                Class.forName("com.android.internal.telephony.ITelephony\$Stub")
            )
        ) as IBinder

        //放回ServiceManager中，替换掉原有的
        val cacheField = smClass.getDeclaredField("sCache")
        cacheField.isAccessible = true
        val cache = cacheField[null] as MutableMap<String, IBinder>
        cache[Context.TELEPHONY_SERVICE] = hookedBinder
        baseHookerHookBuilder?.doLogPrinter("hookSystemServices tms hookITelephony succeed : ${hookedBinder.javaClass.name}")
    }

    // getIMSI
    // getSimSerialNumber
    private fun hookIPhoneSubInfo(rawPhoneSubBinder: IBinder, smClass: Class<*>) {
        val hookedBinder = Proxy.newProxyInstance(
            smClass.classLoader, arrayOf<Class<*>>(IBinder::class.java),
            HookStubHandler(
                rawPhoneSubBinder,
                baseHookerHookBuilder,
                Class.forName("com.android.internal.telephony.IPhoneSubInfo"),
                Class.forName("com.android.internal.telephony.IPhoneSubInfo\$Stub")
            )
        ) as IBinder

        //放回ServiceManager中，替换掉原有的
        val cacheField = smClass.getDeclaredField("sCache")
        cacheField.isAccessible = true
        val cache = cacheField[null] as MutableMap<String, IBinder>
        cache["iphonesubinfo"] = hookedBinder
        baseHookerHookBuilder?.doLogPrinter("hookSystemServices tms hookITelephony succeed : ${hookedBinder.javaClass.name}")
    }

}