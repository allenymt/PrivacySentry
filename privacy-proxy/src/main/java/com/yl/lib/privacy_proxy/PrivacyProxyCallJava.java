package com.yl.lib.privacy_proxy;

import android.content.ClipboardManager;
import android.net.wifi.WifiManager;

import androidx.annotation.Keep;

import com.yl.lib.privacy_annotation.MethodInvokeOpcode;
import com.yl.lib.privacy_annotation.PrivacyClassBlack;
import com.yl.lib.privacy_annotation.PrivacyClassProxy;
import com.yl.lib.privacy_annotation.PrivacyMethodProxy;
import com.yl.lib.sentry.hook.PrivacySentry;
import com.yl.lib.sentry.hook.cache.CachePrivacyManager;
import com.yl.lib.sentry.hook.cache.CacheUtils;
import com.yl.lib.sentry.hook.util.PrivacyClipBoardManager;
import com.yl.lib.sentry.hook.util.PrivacyProxyUtil;

import java.util.Objects;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/**
 * @author yulun
 * @since 2022-11-30 17:47
 * kotlin Boolean 和 java boolean需要特殊处理，不然方法代理不了，在这里直接用java更方便
 */
@PrivacyClassProxy
@Keep
public class PrivacyProxyCallJava {
    @PrivacyMethodProxy(
            originalClass = ClipboardManager.class,
            originalMethod = "hasPrimaryClip",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
    )
    public static boolean hasPrimaryClip(ClipboardManager manager) {
        if (PrivacySentry.Privacy.INSTANCE.getBuilder().isVisitorModel()) {
            return false;
        }
        if (!PrivacyClipBoardManager.Companion.isReadClipboardEnable()) {
            PrivacyProxyUtil.Util.INSTANCE.doFilePrinter("hasPrimaryClip", "读取系统剪贴板是否有值-拦截", "", false, false);
            return false;
        }
        PrivacyProxyUtil.Util.INSTANCE.doFilePrinter("hasPrimaryClip", "读取系统剪贴板是否有值-hasPrimaryClip", "", false, false);
        return manager.hasPrimaryClip();
    }

    /**
     * WIFI是否开启
     */
    @PrivacyMethodProxy(
            originalClass = WifiManager.class,
            originalMethod = "isWifiEnabled",
            originalOpcode = MethodInvokeOpcode.INVOKEVIRTUAL
    )
    public static boolean isWifiEnabled(WifiManager manager) {
        if (Objects.requireNonNull(PrivacySentry.Privacy.INSTANCE.getBuilder()).isVisitorModel() == true) {
            PrivacyProxyUtil.Util.INSTANCE.doFilePrinter("isWifiEnabled", "读取WiFi状态", "", true, false);
            return true;
        }

        String key = "isWifiEnabled";

        return CachePrivacyManager.Manager.INSTANCE.loadWithTimeMemoryCache(
                key,
                "isWifiEnabled",
                true,
                CacheUtils.Utils.MINUTE * 5,
                (new PrivacyProxyCallJavaWifiEnabled(manager)));
    }

    @PrivacyClassBlack
    public static class PrivacyProxyCallJavaWifiEnabled extends Lambda<Boolean> implements Function0<Boolean> {
        final /* synthetic */ WifiManager $manager;

        PrivacyProxyCallJavaWifiEnabled(WifiManager wifiManager) {
            super(0);
            this.$manager = wifiManager;
        }

        public Boolean invoke() {
            return this.$manager.isWifiEnabled();
        }
    }

//    @PrivacyClassBlack
//    public static class PrivacyProxyCallJavaBooleanTransform extends Lambda<Boolean> implements Function0<Boolean> {
//        final /* synthetic */ String value;
//
//        PrivacyProxyCallJavaBooleanTransform(String value) {
//            super(0);
//            this.value = value;
//        }
//
//        public Boolean invoke() {
//            return Boolean.parseBoolean(value);
//        }
//    }
}


