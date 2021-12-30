package com.yl.lib.sentry.hook.hook.ams;

import com.yl.lib.sentry.base.HookMethodManager;
import com.yl.lib.sentry.hook.util.PrivacyUtil;
import com.yl.lib.sentry.hook.hook.BaseHookBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author yulun
 * @sinice 2021-11-16 18:07
 * kotlin动态代理有点问题，用java实现
 */
public class AMSProxy implements InvocationHandler {
    Object iActivityManager;
    BaseHookBuilder baseHookerHookBuilder;

    public AMSProxy(Object iActivityManager, BaseHookBuilder baseHookerHookBuilder) {
        this.iActivityManager = iActivityManager;
        this.baseHookerHookBuilder = baseHookerHookBuilder;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (HookMethodManager.MANAGER.INSTANCE.contains(method.getName(),"","")) {
            try {
                baseHookerHookBuilder.doFilePrinter(HookMethodManager.MANAGER.INSTANCE.findHookItemByName(method.getName()), PrivacyUtil.Util.INSTANCE.getStackTrace());
                return method.invoke(iActivityManager, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return method.invoke(iActivityManager, args);
    }
}
