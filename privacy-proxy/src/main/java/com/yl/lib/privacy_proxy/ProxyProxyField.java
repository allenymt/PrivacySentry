package com.yl.lib.privacy_proxy;

import android.os.Build;

import androidx.annotation.Keep;

import com.yl.lib.privacy_annotation.PrivacyClassProxy;
import com.yl.lib.privacy_annotation.PrivacyFieldProxy;

/**
 * @author yulun
 * @since 2022-03-03 19:42
 * 注意变量的初始化是在类初始化的时候就执行了，所以这里只适合hook不可变的变量
 */
@Keep
@PrivacyClassProxy
public class ProxyProxyField {

    @PrivacyFieldProxy(
            originalClass = android.os.Build.class,
            originalFieldName = "SERIAL"
    )
    public static final String proxySerial = PrivacyProxyCall.Proxy.getSerial();

    // 虽然能保证全局只读取一次，但检测机构是抓包识别的，好像也没什么用，他们好像不能检测变量的读取
    @PrivacyFieldProxy(
            originalClass = android.os.Build.class,
            originalFieldName = "BRAND"
    )
    public static final String proxyBrand = PrivacyProxyCall.Proxy.getBrand();
}
