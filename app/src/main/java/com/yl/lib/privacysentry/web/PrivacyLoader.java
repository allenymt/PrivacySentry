package com.yl.lib.privacysentry.web;

public interface PrivacyLoader extends ILoader {
    void closeSelf();

    void openDialog();

    void close();

    void openPage(String url);
}
