package com.yl.lib.privacysentry.test;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author yulun
 * @since 2022-10-20 10:27
 */
public class TestReflexJava {
    public String c(Context var0, String var1) {
        String var2 = null;

        try {
            TelephonyManager var3 = (TelephonyManager) var0.getSystemService(TELEPHONY_SERVICE);
            Method var4 = TelephonyManager.class.getMethod("getSubscriberId");
            var2 = (String) var4.invoke(var3);
        } catch (Throwable var5) {
        }

        if (TextUtils.isEmpty(var2)) {
            var2 = var1;
        }

        return var2;
    }

    public  String reflex1(Context var0, String var1) {
        String var2 = null;
        try {
            TelephonyManager var3 = (TelephonyManager) var0.getSystemService(TELEPHONY_SERVICE);
            Method var4 = TelephonyManager.class.getMethod("getSubscriberId");
            reflex2(var4, var3);
        } catch (Throwable var5) {
        }
        return var2;
    }

    public  void reflex2(Method var4, Object var3) {

        try {
            String var2 = (String) var4.invoke(var3);
        } catch (Throwable var5) {
        }
    }

    public String reflex3(Context var0, String var1) {
        String var2 = null;
        try {
            InputMethodManager var3 = (InputMethodManager) var0.getSystemService(INPUT_METHOD_SERVICE);
            Method var4 = InputMethodManager.class.getMethod("dasdsad");
            var4.invoke(var3 );
        } catch (Throwable var5) {
        }
        return var2;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void test(Context var1) {
        TelephonyManager var2 = (TelephonyManager) var1.getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        try {
            Class ctm = Class.forName("android.telephony.TelephonyManager");
            Method method = ctm.getDeclaredMethod("getSubscriberId");
            method.invoke(var2);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | SecurityException e) {
            e.printStackTrace();
        }
    }

}
