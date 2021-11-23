package com.yl.lib.sentry.hook.hook;

import android.os.IBinder;

import com.yl.lib.sentry.hook.util.PrivacyUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author yulun
 * @sinice 2021-11-18 14:00
 */
public class HookProxyHandler implements InvocationHandler {
    private Object localProxyBinder;
    private BaseHookBuilder mBaseHookerHookBuilder;

    public HookProxyHandler(IBinder remoteBinder, Class<?> stubClass, BaseHookBuilder baseHookerHookBuilder) {
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
        if (mBaseHookerHookBuilder.getBlackList().keySet().contains(method.getName())) {
            try {
                mBaseHookerHookBuilder.doPrinter(method.getName() , PrivacyUtil.Util.INSTANCE.getStackTrace());
                return method.invoke(localProxyBinder, args);
            } catch (Exception e) {
                if (!(mBaseHookerHookBuilder.getName().equals("tms")
                        && e instanceof InvocationTargetException
                        && ((InvocationTargetException) e).getTargetException() instanceof SecurityException)) {
                    e.printStackTrace();
                }
                // 就目前项目而言，我们hook的方法大部分返回值都是String
                if (method.getGenericReturnType() == String.class){
                    return "";
                }
            }
        }

        // localProxyBinder client进程原始的proxy对象，每次都走反射性能其实很差
        return method.invoke(localProxyBinder, args);
    }
}