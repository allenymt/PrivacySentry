package com.yl.lib.privacysentry.web;

import androidx.annotation.Keep;

/**
 * @author yulun
 * @since 2022-05-09 15:34
 */
@Keep
public class PrivacyJsBridgeProtocol {
    String action;
    String bridgeParam;
    String callBack;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBridgeParam() {
        return bridgeParam;
    }

    public void setBridgeParam(String bridgeParam) {
        this.bridgeParam = bridgeParam;
    }

    public String getCallBack() {
        return callBack;
    }

    public void setCallBack(String callBack) {
        this.callBack = callBack;
    }
}
