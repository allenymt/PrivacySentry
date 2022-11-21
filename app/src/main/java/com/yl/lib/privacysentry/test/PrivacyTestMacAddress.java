package com.yl.lib.privacysentry.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author yulun
 * @since 2022-11-18 10:03
 */
public class PrivacyTestMacAddress {
    public static void getMacAddress() {
        FileInputStream fis_name = null;
        FileInputStream fis = null;
        String mac = "";
        //interfaceName可以直接填写 eth0
        String path = "sys/class/net/eth0/address";

        try {
            fis_name = new FileInputStream(path);

            byte[] buffer_name = new byte[1024 * 8];
            int byteCount_name = fis_name.read(buffer_name);
            if (byteCount_name > 0) {
                mac = new String(buffer_name, 0, byteCount_name, "utf-8");
            }
            if (mac.length() == 0) {
                path = "sys/class/net/eth0/wlan0";
                fis = new FileInputStream(path);
                byte[] buffer = new byte[1024 * 8];
                int byteCount = fis.read(buffer);
                if (byteCount > 0) {
                    mac = new String(buffer, 0, byteCount, "utf-8");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis_name != null) {
                try {
                    fis_name.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }


    public static void testNewFile() {
        String path = "sys/class/net/eth0/address";
        new File(path);
    }
}
