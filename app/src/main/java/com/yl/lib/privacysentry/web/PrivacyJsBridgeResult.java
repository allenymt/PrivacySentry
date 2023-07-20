package com.yl.lib.privacysentry.web;

import androidx.annotation.Keep;


import org.json.JSONObject;

@Keep
public class PrivacyJsBridgeResult {
    String code;
    String result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public static JSONObject buildSuccessJson(String result) {
        JSONObject json = new JSONObject();
        try {
            json.put("code", "0");
            json.put("result", result);
        } catch (Exception e) {
//            android.util.Log.e(JSBridge.TAG, "PrivacyJsBridgeResult toJson() exception", e);
        }

        return json;
    }

    public static JSONObject buildFailJson(String failResponse) {
        JSONObject json = new JSONObject();
        try {
            json.put("code", "1");
            json.put("result", failResponse);
        } catch (Exception e) {
//            android.util.Log.e(JSBridge.TAG, "PrivacyJsBridgeResult toJson() exception", e);
        }
        return json;
    }
}
