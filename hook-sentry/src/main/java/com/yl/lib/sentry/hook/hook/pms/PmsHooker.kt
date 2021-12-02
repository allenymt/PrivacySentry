package com.yl.lib.sentry.hook.hook.pms

import android.app.Application
import android.content.pm.PackageManager
import com.yl.lib.sentry.hook.hook.BaseHookBuilder
import com.yl.lib.sentry.hook.hook.BaseHooker
import java.lang.reflect.Proxy

/**
 * @author yulun
 * @sinice 2021-09-24 14:50
 * 暂时只hook application的packageManager.
 *
 * 那就是Context的实现类里面没有使用静态全局变量来保存PMS的代理对象，
 * 而是每拥有一个Context的实例就持有了一个PMS代理对象的引用；
 * 所以这里有个很蛋疼的事情，那就是我们如果想要完全Hook住PMS，需要精确控制整个进程内部创建的Context对象；
 *
 * 但是对于这个hook来说，application已经足够了
 */
class PmsHooker(baseHookerHookBuilder: BaseHookBuilder?) : BaseHooker(baseHookerHookBuilder) {
    var rawBinder: Any? = null

    override fun hook(ctx: Application) {
        try {
            // 基于ActivityThread hook
            var atClass = Class.forName("android.app.ActivityThread")
            var pmsBinderField = atClass.getDeclaredField("sPackageManager")
            var pmsClass = Class.forName("android.content.pm.IPackageManager")
            pmsBinderField.isAccessible = true
            rawBinder = pmsBinderField.get(null)
            var pmsProxy = Proxy.newProxyInstance(
                pmsBinderField.javaClass.classLoader,
                arrayOf(pmsClass),
                PMSProxy(rawBinder, baseHookerHookBuilder)
            )
            pmsBinderField.set(null, pmsProxy)

            // 2. 替换 ApplicationPackageManager里面的 mPM对象
            val pm: PackageManager = ctx.packageManager
            val mPmField = pm.javaClass.getDeclaredField("mPM")
            mPmField.isAccessible = true
            mPmField.set(pm, pmsProxy)

            baseHookerHookBuilder?.doPrinter("hookSystemServices pms succeed : ${pmsProxy.javaClass.name}")

        } catch (e: Exception) {
            baseHookerHookBuilder?.doPrinter("hookSystemServices pms failed ")
        }
    }

    override fun reduction(ctx: Application) {
        try {
            var atClass = Class.forName("android.app.ActivityThread")
            var pmsBinderField = atClass.getDeclaredField("sPackageManager")
            pmsBinderField.isAccessible = true
            pmsBinderField.set(null, rawBinder)

            // 2. 替换 ApplicationPackageManager里面的 mPM对象
            val pm: PackageManager = ctx.packageManager
            val mPmField = pm.javaClass.getDeclaredField("mPM")
            mPmField.isAccessible = true
            mPmField.set(pm, rawBinder)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}