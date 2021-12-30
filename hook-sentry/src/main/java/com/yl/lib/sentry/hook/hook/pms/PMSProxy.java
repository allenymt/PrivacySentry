package com.yl.lib.sentry.hook.hook.pms;

import com.yl.lib.sentry.base.HookMethodManager;
import com.yl.lib.sentry.hook.util.PrivacyUtil;
import com.yl.lib.sentry.hook.hook.BaseHookBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author yulun
 * @sinice 2021-11-18 17:16
 */
class PMSProxy implements InvocationHandler {
    Object proxyBinder;
    BaseHookBuilder baseHookerHookBuilder;

    public PMSProxy(Object proxyBinder, BaseHookBuilder baseHookerHookBuilder) {
        this.proxyBinder = proxyBinder;
        this.baseHookerHookBuilder = baseHookerHookBuilder;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (HookMethodManager.MANAGER.INSTANCE.contains(method.getName(),"","")) {
            try {
                baseHookerHookBuilder.doFilePrinter(HookMethodManager.MANAGER.INSTANCE.findHookItemByName(method.getName()), PrivacyUtil.Util.INSTANCE.getStackTrace());
                return method.invoke(proxyBinder, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return method.invoke(proxyBinder, args);
    }
}
