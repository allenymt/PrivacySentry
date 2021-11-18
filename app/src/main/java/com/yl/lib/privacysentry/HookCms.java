package com.yl.lib.privacysentry;

import android.content.ClipData;
import android.content.Context;
import android.os.IBinder;

import com.yl.lib.sentry.hook.util.PrivacyLog;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author yulun
 * @sinice 2021-11-17 17:12
 * 系统剪贴板服务hook
 */
class HookCms {
    public static void hook() {
        try {
            // 从ClipboardManager源码看，最终走的是ServiceManager mService = IClipboard.Stub.asInterface(
            //                ServiceManager.getServiceOrThrow(Context.CLIPBOARD_SERVICE));
            //下面这一段的意思其实就是ServiceManager.getService("clipboard")
            //只不过ServiceManager这个类是@hide的
            Class<?> serviceManager = Class.forName("android.os.ServiceManager");
            // ServiceManager里的cache是静态变量，这就为hook提供了入口
            Method getService = serviceManager.getDeclaredMethod("getService", String.class);
            //取得ServiceManager里的原始的clipboard binder对象
            //一般来说这是一个Binder代理对象
            IBinder rawBinder = (IBinder) getService.invoke(null, Context.CLIPBOARD_SERVICE);

            //Hook掉这个Binder代理的queryLocalInterface 方法
            //然后在queryLocalInterface返回一个IInterface对象，hook掉我们感兴趣的方法即可
            // 主要下这里，为什么第二个参数传IBinder.class这个接口就行了，这就要看我们在动态代理生成的类里实际要拦截的方法是什么
            IBinder hookedBinder = (IBinder) Proxy.newProxyInstance(
                    serviceManager.getClassLoader(),
                    new Class<?>[]{IBinder.class},
                    new ClipboardStubHandler(rawBinder));

            //放回ServiceManager中，替换掉原有的
            Field cacheField = serviceManager.getDeclaredField("sCache");
            cacheField.setAccessible(true);
            @SuppressWarnings({"unchecked"})
            Map<String, IBinder> cache = (Map<String, IBinder>) cacheField.get(null);
            cache.put(Context.CLIPBOARD_SERVICE, hookedBinder);
        } catch (Exception e) {

        }
    }

    // 这个代理对象只是拦截stub对象
    static class ClipboardStubHandler implements InvocationHandler {
        // 原始的Server binder对象，
        private IBinder rawServerBinder;
        // 本地proxy对象待实现的接口,也就是本地aidl待实现的接口，在cms里就是android.content.IClipboard
        private Class iInterface;
        // stub类。aidl生成的stub类
        private Class stubClass;

        public ClipboardStubHandler(IBinder rawServerBinder) {
            this.rawServerBinder = rawServerBinder;
            try {
                this.iInterface = Class.forName("android.content.IClipboard");
                this.stubClass = Class.forName("android.content.IClipboard$Stub");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            PrivacyLog.Log.i("ClipboardStubHandler " + method.getName() + " is invoked");
            // queryLocalInterface是每个binder都有的方法，一般是在asInterface里被调用，返回的就是aidl里的接口
            // asInterface这个方法在Server进程返回的就是aidl里的接口，如果是在client进程，返回的就是proxy对象
            if ("queryLocalInterface".equals(method.getName())) {
                //这里不能拦截具体的服务的方法，因为这是一个远程的Binder，也就是stub，还没有转化为本地Binder对象
                //所以先拦截我们所知的queryLocalInterface方法，返回一个本地Binder对象的代理
                return Proxy.newProxyInstance(rawServerBinder.getClass().getClassLoader(),
                        //iInterface就是当前aidl要实现的接口
                        new Class[]{this.iInterface},
                        // remoteBinder 原始binder
                        // stubClass 当前aidl生成的stub类
                        new ClipboardProxyHandler(rawServerBinder, stubClass));
            }

            return method.invoke(rawServerBinder, args);
        }
    }

    public static class ClipboardProxyHandler implements InvocationHandler {
        private Object localProxyBinder;

        public ClipboardProxyHandler(IBinder remoteBinder, Class<?> stubClass) {
            try {
                // hook asInterface方法
                Method asInterfaceMethod = stubClass.getMethod("asInterface", IBinder.class);
                // 反射调用获取到本地的proxy对象，这个proxy对象就是ServiceManger里在client进程拿到的proxy对象
                localProxyBinder = asInterfaceMethod.invoke(null, remoteBinder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            PrivacyLog.Log.i("ClipboardProxyHandler localProxyBinder " + method.getName() + "() is invoked");
            String methodName = method.getName();
            // 拦截方法
            if ("setPrimaryClip".equals(methodName)) {
                //这里对setPrimaryClip()进行了拦截
                int argsLength = args.length;
                if (argsLength >= 2 && args[0] instanceof ClipData) {
                    ClipData data = (ClipData) args[0];
                    String text = data.getItemAt(0).getText().toString();
                    text += "   我拦截了你写入系统剪贴板的服务";
                    args[0] = ClipData.newPlainText(data.getDescription().getLabel(), text);
                }
            }

            // localProxyBinder client进程原始的proxy对象，每次都走反射性能其实很差
            return method.invoke(localProxyBinder, args);
        }
    }
}
