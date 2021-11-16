package com.yl.lib.sentry.hook.hook
import android.app.ActivityManager
import android.os.Build
import com.yl.lib.sentry.hook.util.PrivacyUtil
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * @author yulun
 * @sinice 2021-09-24 15:32
 */
class AmsHooker(baseHookerHookBuilder: BaseHookBuilder?) : BaseHooker(baseHookerHookBuilder) {

    override fun hook() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        try {
            val defaultSingleton: Any
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
            val iAMs = mInstance[defaultSingleton]
            val iAmClazz = Class.forName("android.app.IActivityManager")
            val proxy = Proxy.newProxyInstance(
                Thread.currentThread().contextClassLoader,
                arrayOf(iAmClazz),
                baseHookerHookBuilder?.let { AMSProxy(iAMs, it) }
            )
            mInstance[defaultSingleton] = proxy
            if (proxy === mInstance[defaultSingleton]) {
                baseHookerHookBuilder?.doPrinter("hookSystemServices succeed : $proxy")
            }
        } catch (e: Exception) {
            baseHookerHookBuilder?.doPrinter("hookSystemServices failed\"")
        }
    }

    class AMSProxy internal constructor(
        private val iActivityManager: Any,
        private val baseHookerHookBuilder: BaseHookBuilder
    ) : InvocationHandler {
        @Throws(Throwable::class)
        override fun invoke(obj: Any, method: Method, args: Array<Any>): Any {

            if (baseHookerHookBuilder.blackList.contains(method.name)) {
                try {
                    baseHookerHookBuilder?.doPrinter(
                        " method name is  ${method.name} args length is : ${if (args != null && args.isNotEmpty()) args[0] else ""}" + args.size
                    )
                    PrivacyUtil.Util.getStackTrace()?.let { baseHookerHookBuilder?.doPrinter(it) }
                    return method.invoke(iActivityManager, *args)
                } catch (e: Exception) {
                    baseHookerHookBuilder?.doPrinter(
                        "checkPermission hook failed!" + e.message
                    )
                }
            }
            return method.invoke(iActivityManager, *args)
        }
    }


}