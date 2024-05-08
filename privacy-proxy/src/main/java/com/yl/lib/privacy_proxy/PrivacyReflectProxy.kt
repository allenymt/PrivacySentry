package com.yl.lib.privacy_proxy

import android.bluetooth.BluetoothAdapter
import android.net.wifi.WifiInfo
import android.telephony.TelephonyManager
import androidx.annotation.Keep
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import com.yl.lib.privacy_annotation.PrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyMethodProxy
import com.yl.lib.sentry.hook.cache.CachePrivacyManager
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil
import java.lang.reflect.Method
import java.net.NetworkInterface

/**
 * @author yulun
 * @since 2022-06-17 17:56
 * 代理反射
 */
@Keep
open class PrivacyReflectProxy {

    @Keep
    @PrivacyClassProxy
    object ReflectProxy {

        // 这个方法的注册放在了PrivacyProxyCall2中，提供了一个java注册的例子
        @PrivacyMethodProxy(
            originalClass = Method::class,
            originalMethod = "invoke",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
        )
        @JvmStatic
        fun invoke(
            method: Method,
            obj: Any?,
            vararg args: Any?
        ): Any? {
            if (obj is WifiInfo) {
                if ("getMacAddress" == method.name) {
                    if (args.isEmpty()) return PrivacyProxyCall.Proxy.getMacAddress(obj)
                }
            }

            if (obj is TelephonyManager) {
                if ("getMeid" == method.name && args.isEmpty()) {
                    return PrivacyTelephonyProxy.TelephonyProxy.getMeid(obj)
                }
                if ("getMeid" == method.name && args.size == 1 && args[0] is Int) {
                    return PrivacyTelephonyProxy.TelephonyProxy.getMeid(
                        obj,
                        args[0] as Int
                    )
                }
                if ("getDeviceId" == method.name && args.isEmpty()) {
                    return PrivacyTelephonyProxy.TelephonyProxy.getDeviceId(obj)
                }
                if ("getDeviceId" == method.name && args.size == 1 && args[0] is Int) {
                    return PrivacyTelephonyProxy.TelephonyProxy.getDeviceId(
                        obj,
                        args[0] as Int
                    )
                }
                if ("getSubscriberId" == method.name && args.isEmpty()) {
                    return PrivacyTelephonyProxy.TelephonyProxy.getSubscriberId(obj)
                }
                if ("getSubscriberId" == method.name && args.size == 1 && args.get(0) is Int) {
                    return PrivacyTelephonyProxy.TelephonyProxy.getSubscriberId(
                        obj,
                        args[0] as Int
                    )
                }
                if ("getImei" == method.name && args.isEmpty()) {
                    return PrivacyTelephonyProxy.TelephonyProxy.getImei(obj)
                }
                if ("getImei" == method.name && args.size == 1 && args[0] is Int) {
                    return PrivacyTelephonyProxy.TelephonyProxy.getImei(
                        obj,
                        args[0] as Int
                    )
                }
                if ("getSimSerialNumber" == method.name && args.isEmpty()) {
                    return PrivacyTelephonyProxy.TelephonyProxy.getSimSerialNumber(obj)
                }
                if ("getSimSerialNumber" == method.name && args.size == 1 && args[0] is Int) {
                    return PrivacyTelephonyProxy.TelephonyProxy.getSimSerialNumber(
                        obj,
                        args[0] as Int
                    )
                }
            }

            if (obj is NetworkInterface) {
                if ("getHardwareAddress" == method.name && args.isEmpty()) {
                    return PrivacyProxyCall.Proxy.getHardwareAddress(obj)
                }
            }

            if (obj is BluetoothAdapter) {
                if ("getAddress" == method.name && args.isEmpty()) {
                    return PrivacyProxyCall.Proxy.getAddress(obj)
                }
            }

            // 针对OAID AAID VAID的方法，特殊处理，不做映射了
            var methodName = method.name.toUpperCase()
            if (methodName.contains("OAID") || methodName.contains("AAID") || methodName.contains("VAID")){
                val cacheKey = obj?.javaClass?.name +"_"+ method.name
                PrivacyProxyUtil.Util.doFilePrinter("methodName", cacheKey)
                return CachePrivacyManager.Manager.loadWithMemoryCache(
                    cacheKey,
                    cacheKey,
                    ""
                ) { method.invoke(obj, *args) }
            }

            if (obj?.javaClass?.name?.equals("com.android.id.impl.IdProviderImpl") == true){
                return method.invoke(obj, *args)
            }
            return method.invoke(obj, *args)
        }
    }
}