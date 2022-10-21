package com.yl.lib.privacy_proxy

import android.bluetooth.BluetoothAdapter
import android.net.wifi.WifiInfo
import android.telephony.TelephonyManager
import androidx.annotation.Keep
import com.yl.lib.privacy_annotation.MethodInvokeOpcode
import com.yl.lib.privacy_annotation.PrivacyClassProxy
import com.yl.lib.privacy_annotation.PrivacyMethodProxy
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
                    return PrivacyProxyCall.Proxy.getMeid(obj)
                }
                if ("getMeid" == method.name && args.size == 1 && args[0] is Int) {
                    return PrivacyProxyCall.Proxy.getMeid(
                        obj,
                        args[0] as Int
                    )
                }
                if ("getDeviceId" == method.name && args.isEmpty()) {
                    return PrivacyProxyCall.Proxy.getDeviceId(obj)
                }
                if ("getDeviceId" == method.name && args.size == 1 && args[0] is Int) {
                    return PrivacyProxyCall.Proxy.getDeviceId(
                        obj,
                        args[0] as Int
                    )
                }
                if ("getSubscriberId" == method.name && args.isEmpty()) {
                    return PrivacyProxyCall.Proxy.getSubscriberId(obj)
                }
                if ("getSubscriberId" == method.name && args.size == 1 && args.get(0) is Int) {
                    return PrivacyProxyCall.Proxy.getSubscriberId(
                        obj,
                        args[0] as Int
                    )
                }
                if ("getImei" == method.name && args.isEmpty()) {
                    return PrivacyProxyCall.Proxy.getImei(obj)
                }
                if ("getImei" == method.name && args.size == 1 && args[0] is Int) {
                    return PrivacyProxyCall.Proxy.getImei(
                        obj,
                        args[0] as Int
                    )
                }
                if ("getSimSerialNumber" == method.name && args.isEmpty()) {
                    return PrivacyProxyCall.Proxy.getSimSerialNumber(obj)
                }
                if ("getSimSerialNumber" == method.name && args.size == 1 && args[0] is Int) {
                    return PrivacyProxyCall.Proxy.getSimSerialNumber(
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

            return method.invoke(obj, *args)
        }
    }
}