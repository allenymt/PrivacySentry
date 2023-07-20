package com.yl.lib.privacysentry.web;

import android.text.TextUtils;

import org.json.JSONObject;


public class PrivacyJsBridge {
    /**
     * 处理H5传过来的协议数据
     *
     * @param data
     * @param loader
     */
    public static void
    handle(String data, PrivacyLoader loader) {
        PrivacyJsBridgeProtocol adJsBridgeProtocol = null;
        try {
//            adJsBridgeProtocol = JSONObject.parseObject(data, PrivacyJsBridgeProtocol.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (adJsBridgeProtocol == null) {
            return;
        }


        switch (adJsBridgeProtocol.action) {
            case "showPrivacyAlert":
                try {
                    showPrivacyAlert(adJsBridgeProtocol, loader);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "closeSelf":
                try {
                    closeSelf(adJsBridgeProtocol, loader);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "openPage":
                try {
                    if (null != adJsBridgeProtocol.bridgeParam) {
//                        JSONObject json = JSONObject.parseObject(adJsBridgeProtocol.bridgeParam);
//                        if (json.containsKey("url")) {
//                            String url = json.get("url").toString();
//                            openPage(adJsBridgeProtocol, loader, url);
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            default:
                break;
        }
    }

    private static void openPage(PrivacyJsBridgeProtocol protocol, PrivacyLoader loader, String url) {
        if (!TextUtils.isEmpty(protocol.getCallBack())) {
            callJs(loader, protocol.getCallBack(), PrivacyJsBridgeResult.buildSuccessJson("").toString());
        }
        loader.openPage(url);
    }

    private static void showPrivacyAlert(PrivacyJsBridgeProtocol protocol, PrivacyLoader loader) {
        if (!TextUtils.isEmpty(protocol.getCallBack())) {
            callJs(loader, protocol.getCallBack(), PrivacyJsBridgeResult.buildSuccessJson("").toString());
        }
        loader.openDialog();
    }

    private static void closeSelf(PrivacyJsBridgeProtocol protocol, PrivacyLoader loader) {
        if (!TextUtils.isEmpty(protocol.getCallBack())) {
            callJs(loader, protocol.getCallBack(), PrivacyJsBridgeResult.buildSuccessJson("").toString());
        }
        loader.closeSelf();
    }

    private static boolean callJs(PrivacyLoader loader, String callBackName, String result) {
        String call = String.format("javascript:%s(%s);", callBackName, result);
        loader.loadUrl(call);
        return true;
    }

}
