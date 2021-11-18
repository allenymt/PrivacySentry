package com.yl.lib.sentry.hook.hook;

import android.os.IBinder;

import com.yl.lib.sentry.hook.util.PrivacyLog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author yulun
 * @sinice 2021-11-18 14:00
 */
public class HookStubHandler implements InvocationHandler {
    // 原始的Server binder对象，
    private IBinder rawServerBinder;
    // 本地proxy对象待实现的接口,也就是本地aidl待实现的接口，在cms里就是android.content.IClipboard
    private Class iInterface;
    // stub类。aidl生成的stub类
    private Class stubClass;
    private BaseHookBuilder mBaseHookerHookBuilder;

    public HookStubHandler(IBinder rawServerBinder, BaseHookBuilder baseHookerHookBuilder,Class iInterface,Class stubClass) {
        this.rawServerBinder = rawServerBinder;
        try {
            this.iInterface = iInterface;
            this.stubClass = stubClass;
            mBaseHookerHookBuilder = baseHookerHookBuilder;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // queryLocalInterface是每个binder都有的方法，一般是在asInterface里被调用，返回的就是aidl里的接口
        // asInterface这个方法在Server进程返回的就是aidl里的接口，如果是在client进程，返回的就是proxy对象
        if ("queryLocalInterface".equals(method.getName())) {
            //这里不能拦截具体的服务的方法，因为这是一个远程的Binder，也就是stub，还没有转化为本地Binder对象
            //所以先拦截我们所知的queryLocalInterface方法，返回一个本地Binder对象的代理
            mBaseHookerHookBuilder.doPrinter("拦截queryLocalInterface");
            return Proxy.newProxyInstance(rawServerBinder.getClass().getClassLoader(),
                    //iInterface就是当前aidl要实现的接口
                    new Class[]{this.iInterface},
                    // remoteBinder 原始binder
                    // stubClass 当前aidl生成的stub类
                    new HookProxyHandler(rawServerBinder, stubClass, mBaseHookerHookBuilder));
        }

        return method.invoke(rawServerBinder, args);
    }
}