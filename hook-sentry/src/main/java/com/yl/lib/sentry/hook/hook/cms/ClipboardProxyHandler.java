package com.yl.lib.sentry.hook.hook.cms;

import android.os.IBinder;

import com.yl.lib.sentry.hook.hook.BaseHookBuilder;
import com.yl.lib.sentry.hook.util.PrivacyUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author yulun
 * @sinice 2021-11-18 14:00
 */
public class ClipboardProxyHandler implements InvocationHandler {
    private Object localProxyBinder;
    private BaseHookBuilder mBaseHookerHookBuilder;

    public ClipboardProxyHandler(IBinder remoteBinder, Class<?> stubClass, BaseHookBuilder baseHookerHookBuilder) {
        try {
            // hook asInterface方法
            Method asInterfaceMethod = stubClass.getMethod("asInterface", IBinder.class);
            // 反射调用获取到本地的proxy对象，这个proxy对象就是ServiceManger里在client进程拿到的proxy对象
            localProxyBinder = asInterfaceMethod.invoke(null, remoteBinder);
            mBaseHookerHookBuilder = baseHookerHookBuilder;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (mBaseHookerHookBuilder.getBlackList().contains(method.getName())) {
            try {
                mBaseHookerHookBuilder.doPrinter(" method name is  " + method.getName() + "args length is : " + (args == null ? String.valueOf(0) : String.valueOf(args.length)));
                mBaseHookerHookBuilder.doPrinter(PrivacyUtil.Util.INSTANCE.getStackTrace());
                return method.invoke(localProxyBinder, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // localProxyBinder client进程原始的proxy对象，每次都走反射性能其实很差
        return method.invoke(localProxyBinder, args);
    }
}