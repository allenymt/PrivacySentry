package com.yl.lib.privacy_proxy;

import androidx.annotation.Keep;

import com.yl.lib.privacy_annotation.PrivacyClassProxy;
import com.yl.lib.privacy_annotation.PrivacyFieldProxy;

/**
 * @author yulun
 * @since 2022-03-03 19:42
 * kotlin里的伴生对象 定义的常量编译后都是private
 */
@Keep
@PrivacyClassProxy
public class ProxyProxyField {

    @PrivacyFieldProxy(
            originalClass = android.os.Build.class,
            originalFieldName = "SERIAL"
    )
    public static final String proxySerial = PrivacyProxyCall.Proxy.getSerial();
}
