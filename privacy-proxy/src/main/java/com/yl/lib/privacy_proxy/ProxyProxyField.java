package com.yl.lib.privacy_proxy;

import android.os.Build;

import androidx.annotation.RequiresApi;


/**
 * @author yulun
 * @since 2022-03-03 19:42
 * kotlin里的伴生对象 定义的常量编译后都是private
 */
public class ProxyProxyField {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static final String proxySerial = PrivacyProxyCall.Proxy.getSerial();
}
