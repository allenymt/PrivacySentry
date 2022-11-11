package com.yl.lib.privacysentry.test

import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.telephony.TelephonyManager
import com.yl.lib.sentry.hook.util.ReflectUtils
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * @author yulun
 * @since 2022-10-20 10:21
 */
class TestReflex {
    fun test(var1: Context) {
        val var2 = var1.applicationContext.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        try {
            val ctm = Class.forName("android.telephony.TelephonyManager")
            val method = ctm.getDeclaredMethod("getDeviceId", Int::class.javaPrimitiveType)
            method.invoke(var2, 2)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

    fun test2(obj: Any,
              name: String,
              types: Array<Class<*>>,
              args: Array<Any?>){
        try {
            val method: Method? =
               getMethod(obj.javaClass.superclass, name, types)
            if (null != method) {
                method.isAccessible = true
                method.invoke(obj, *args)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private fun getMethod(klass: Class<*>, name: String, types: Array<Class<*>>): Method? {
        return try {
            klass.getDeclaredMethod(name, *types)
        } catch (e: NoSuchMethodException) {
            val parent = klass.superclass ?: return null
            getMethod(parent, name, types)
        }
    }
}