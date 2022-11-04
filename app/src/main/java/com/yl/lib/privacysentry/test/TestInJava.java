package com.yl.lib.privacysentry.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.yl.lib.sentry.hook.PrivacySentry;
import com.yl.lib.sentry.hook.PrivacySentryBuilder;
import com.yl.lib.sentry.hook.util.PrivacyLog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author yulun
 * @sinice 2021-12-26 13:30
 */
public class TestInJava {

    public static void testInitJava() {
        // 完整版配置
        PrivacySentryBuilder builder = new PrivacySentryBuilder()
                // 自定义文件结果的输出名
                .configResultFileName("demo_test")
                //自定义检测时间，也支持主动停止检测 PrivacySentry.Privacy.stopWatch()
                .configWatchTime(10 * 60 * 1000);
        // 添加默认结果输出，包含log输出和文件输出
        PrivacySentry.Privacy.INSTANCE.init(null, builder);
    }

    // 获取有限网IP
    public static String getHostIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
        }
        return "0.0.0.0";
    }

    @SuppressLint("MissingPermission")
    public static String getIpAddress(Context context) {
        if (context == null) {
            return "";
        }
        ConnectivityManager conManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo info = conManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 3/4g网络
                if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return getHostIp();
                } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    return getOutNetIP(); // 外网地址
                } else if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    // 以太网有限网络
                    return getHostIp();
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    /**
     * 获取外网ip地址（非本地局域网地址）的方法
     */
    public static String getOutNetIP() {
        String ipAddress = "";
        try {
            String address = "https://ip.taobao.com/service/getIpInfo2.php?ip=myip";
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.7 Safari/537.36"); //设置浏览器ua 保证不出现503
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();
                // 将流转化为字符串
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in));
                String tmpString;
                StringBuilder retJSON = new StringBuilder();
                while ((tmpString = reader.readLine()) != null) {
                    retJSON.append(tmpString + "\n");
                }
                JSONObject jsonObject = new JSONObject(retJSON.toString());
                String code = jsonObject.getString("code");
                PrivacyLog.Log.e("提示：" + retJSON.toString());
                if (code.equals("0")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    ipAddress = data.getString("ip")/* + "(" + data.getString("country")
              + data.getString("area") + "区"
              + data.getString("region") + data.getString("city")
              + data.getString("isp") + ")"*/;
                    PrivacyLog.Log.e("您的IP地址是：" + ipAddress);
                } else {
                    PrivacyLog.Log.e("IP接口异常，无法获取IP地址！");
                }
            } else {
                PrivacyLog.Log.e("网络连接异常，无法获取IP地址！");
            }
        } catch (Exception e) {
            PrivacyLog.Log.e("获取IP地址时出现异常，异常信息是：" + e.toString());
        }
        return ipAddress;
    }

    public static void testReflexClipManager() {
        try {
            Class<?> companionClass = Class.forName("com.yl.lib.sentry.hook.util.PrivacyClipBoardManager$Companion");
            Class<?> privacyClipBoardManagerClass = Class.forName("com.yl.lib.sentry.hook.util.PrivacyClipBoardManager");
            Field companion = privacyClipBoardManagerClass.getField("Companion");
            Method m1 = companionClass.getDeclaredMethod("isReadClipboardEnable", (Class[]) null);
            boolean open = (Boolean) m1.invoke(companion.get(null), (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testReflexClipManagerOpen() {
        try {
            Class<?> companionClass = Class.forName("com.yl.lib.sentry.hook.util.PrivacyClipBoardManager$Companion");
            Class<?> privacyClipBoardManagerClass = Class.forName("com.yl.lib.sentry.hook.util.PrivacyClipBoardManager");
            Field companion = privacyClipBoardManagerClass.getField("Companion");
            Method m1 = companionClass.getDeclaredMethod("openReadClipboard", (Class[]) null);
            m1.invoke(companion.get(null), (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testReflexClipManagerClose() {
        try {
            Class<?> companionClass = Class.forName("com.yl.lib.sentry.hook.util.PrivacyClipBoardManager$Companion");
            Class<?> privacyClipBoardManagerClass = Class.forName("com.yl.lib.sentry.hook.util.PrivacyClipBoardManager");
            Field companion = privacyClipBoardManagerClass.getField("Companion");
            Method m1 = companionClass.getDeclaredMethod("closeReadClipboard", (Class[]) null);
            m1.invoke(companion.get(null), (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
