package com.yl.lib.privacysentry.test;

import android.content.Context;
import android.os.Build;

import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 * @author yulun
 * @since 2024-04-25 15:43
 */
public class TestOaidGetter {
    public static String getOaid(Context var0) {
        try {
            String var1 = "com.android.id.impl.IdProviderImpl";
            Class var2 = a(var0, var1);
            Object var3 = var2.newInstance();
            String var4 = "getOAID";
            Method var5 = var2.getMethod(var4, Context.class);
            String var6 = "getAAID";
            Method var7 = var2.getMethod(var6, Context.class);
            String var8 = "getVAID";
            Method var9 = var2.getMethod(var8, Context.class);
            Object var10 = var9.invoke(var3, var0);
            Object var11 = var7.invoke(var3, var0);
            Object var12 = var5.invoke(var3, var0);
            JSONObject var13 = new JSONObject();
            var13.put("joad", var12);
            var13.put("jvad", var11);
            var13.put("jaad", var10);
            return var13.toString();
        } catch (Throwable var14) {
            return "";
        }
    }

    private static Class<?> a(Context var0, String var1) throws ClassNotFoundException {
        if (var1 != null && var1.trim().length() != 0) {
            boolean var2 = var0 != null;
            if (var2 && Build.VERSION.SDK_INT >= 29) {
                try {
                    return var0.getClassLoader().loadClass(var1);
                } catch (ClassNotFoundException var5) {
                }
            }

            try {
                return Class.forName(var1);
            } catch (ClassNotFoundException var4) {
                throw new ClassNotFoundException("loadClass fail ", var4);
            }
        } else {
            throw new ClassNotFoundException("class is empty");
        }
    }
}
