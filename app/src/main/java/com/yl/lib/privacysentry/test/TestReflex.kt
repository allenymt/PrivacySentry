package com.yl.lib.privacysentry.test

import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.telephony.TelephonyManager
import java.lang.reflect.InvocationTargetException

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
}