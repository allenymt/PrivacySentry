package com.yl.lib.privacysentry.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;

import androidx.annotation.RequiresApi;

import com.yl.lib.privacy_annotation.MethodInvokeOpcode;
import com.yl.lib.privacy_annotation.PrivacyMethodProxy;
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
import java.net.URLConnection;
import java.util.Enumeration;

/**
 * @author yulun
 * @sinice 2021-12-26 13:30
 */
public class TestInJava {

    // 测试hook http url connection
    // 思考： 如何解决还没有暴露的合规问题？比如某天规则增加了？线上如何做？如何减少调整？你有没有比较好的解决方案?
    // 由于是基于编译期，那是否可以通过 替换新的产物来解决，通过patch的方式，因为本质上是替换方法的调用
    // 动态替换拦不了的
    public static void testHttpUrlConnection() {

        URL url = null;
        try {
            url = new URL("https://www.baidu.com");
            URLConnection rulConnection = url.openConnection();// 此处的urlConnection对象实际上是根据URL的
            HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;
            // 设定请求的方法为"POST"，默认是GET
            httpUrlConnection.setRequestMethod("POST");
            // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
            // http正文内，因此需要设为true, 默认情况下是false;
            httpUrlConnection.setDoOutput(true);
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpUrlConnection.setDoInput(true);
            // Post 请求不能使用缓存
            httpUrlConnection.setUseCaches(false);
            // 设定传送的内容类型是可序列化的java对象
            // (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
            httpUrlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
            // 连接，从上述url.openConnection()至此的配置必须要在connect之前完成，
            httpUrlConnection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

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
            ex.printStackTrace();
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
                    getIPByWifiInfo(context);
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

    public static void getIPByWifiInfo(Context context){
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = Formatter.formatIpAddress(ipAddress);
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
