package com.yl.lib.sentry.hook.hook.cms

import android.content.Context
import android.os.IBinder
import com.yl.lib.sentry.hook.hook.BaseHookBuilder
import com.yl.lib.sentry.hook.hook.BaseHooker
import com.yl.lib.sentry.hook.hook.HookStubHandler
import java.lang.reflect.Proxy

/**
 * @author yulun
 * @sinice 2021-11-18 13:58
 * 剪贴板服务 hook
 * 这种hook方式也有个问题，在hook前当前进程不能调用剪贴板服务，不然要到ContextIml的cache里去重置
 * 所以整个SDK越早初始化越好
 */
class CmsHooker(baseHookerHookBuilder: BaseHookBuilder?) : BaseHooker(baseHookerHookBuilder) {
    override fun hook(ctx: Context) {
        try {
            // 从ClipboardManager源码看，最终走的是ServiceManager mService = IClipboard.Stub.asInterface(
            //                ServiceManager.getServiceOrThrow(Context.CLIPBOARD_SERVICE));
            //下面这一段的意思其实就是ServiceManager.getService("clipboard")
            //只不过ServiceManager这个类是@hide的
            val serviceManager = Class.forName("android.os.ServiceManager")
            // ServiceManager里的cache是静态变量，这就为hook提供了入口
            val getService = serviceManager.getDeclaredMethod(
                "getService",
                String::class.java
            )
            //取得ServiceManager里的原始的clipboard binder对象
            //一般来说这是一个Binder代理对象
            val rawBinder = getService.invoke(null, Context.CLIPBOARD_SERVICE) as IBinder

            //Hook掉这个Binder代理的queryLocalInterface 方法
            //然后在queryLocalInterface返回一个IInterface对象，hook掉我们感兴趣的方法即可
            // 主要下这里，为什么第二个参数传IBinder.class这个接口就行了，这就要看我们在动态代理生成的类里实际要拦截的方法是什么
            val hookedBinder = Proxy.newProxyInstance(
                serviceManager.classLoader, arrayOf<Class<*>>(IBinder::class.java),
                HookStubHandler(
                    rawBinder,
                    baseHookerHookBuilder,
                    Class.forName("android.content.IClipboard"),
                    Class.forName("android.content.IClipboard\$Stub")
                )
            ) as IBinder

            //放回ServiceManager中，替换掉原有的
            val cacheField = serviceManager.getDeclaredField("sCache")
            cacheField.isAccessible = true
            val cache = cacheField[null] as MutableMap<String, IBinder>
            cache[Context.CLIPBOARD_SERVICE] = hookedBinder
            baseHookerHookBuilder?.doPrinter("hookSystemServices cms succeed : ${hookedBinder.javaClass.name}")
        } catch (e: Exception) {
            e.printStackTrace()
            baseHookerHookBuilder?.doPrinter("hookSystemServices cms failed")
        }
    }
}