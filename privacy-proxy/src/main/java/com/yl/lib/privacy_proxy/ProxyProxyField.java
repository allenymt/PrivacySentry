package com.yl.lib.privacy_proxy;

import androidx.annotation.Keep;

/**
 * @author yulun
 * @since 2022-03-03 19:42
 * kotlin里的伴生对象 定义的常量编译后都是private
 */
@Keep
public class ProxyProxyField {

    public static final String proxySerial = PrivacyProxyCall.Proxy.getSerial();
}
