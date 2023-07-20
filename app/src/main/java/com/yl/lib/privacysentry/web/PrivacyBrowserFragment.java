package com.yl.lib.privacysentry.web;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.yl.lib.privacysentry.R;

public class PrivacyBrowserFragment extends Fragment {

    private WebView webView;

    private String url;
    private boolean canIntercept = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        url = bundle.getString("url");
        canIntercept = bundle.getBoolean("canIntercept");
        markIntercept(!canIntercept);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.wdb_privacy_browser_activity, container, false);
        webView = root.findViewById(R.id.static_webview);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);//设置与Js交互的权限
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        bindJSBridge();

        webView.loadUrl(url);
//        WDBStatusBarUtils.setLightTheme(getActivity());
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        markIntercept(true);
    }


    private void bindJSBridge() {
        webView.addJavascriptInterface(new JSBridgeCall(), "WdPrivacyBrowserJSInterface");
    }

    PrivacyLoader iLoader = new PrivacyLoader() {
        @Override
        public void closeSelf() {
            getActivity().getSupportFragmentManager().popBackStack();
        }

        @Override
        public void openDialog() {
            openBrowserDialog();
        }

        @Override
        public void close() {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }

        @Override
        public void openPage(String url) {
            openDetailPage(url);
        }

        @Override
        public void loadUrl(String url) {
            if (webView != null) {
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl(url);
                    }
                });
            }
        }
    };

    private void openDetailPage(String url) {
        try {
            PrivacyBrowserFragment fragment = new PrivacyBrowserFragment();
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            bundle.putBoolean("canIntercept", true);
            fragment.setArguments(bundle);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, fragment).addToBackStack(null).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void openBrowserDialog() {
//        PrivacyDialog dialog = PrivacyDialog.newPrivacyDialog(BeforeApplicationInitHelper.getInstance(getContext()).isNewUser(), getActivity().getSupportFragmentManager());
//        PrivacyAuthorManager.manager.INSTANCE.setPrivacy(new PrivacyDialog.AuthorizationPrivacy() {
//
//            @Override
//            public void access() {
//                goToMain();
//            }
//
//            @Override
//            public void refuse() {
//            }
//        });
//        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//        ft.add(android.R.id.content, dialog).addToBackStack(null).show(dialog).commit();
    }

    private void goToMain() {
//        WDMessage message = WDMessage.builder()
//                .route("privacy_browser")
//                .build();
//        WDMessager.getInstance().post(message);
    }


    private void markIntercept(boolean intercept) {
//        if (getActivity() instanceof PrivacyPopController) {
//            ((PrivacyPopController) getActivity()).markInterceptBack(intercept);
//        }
    }

    public class JSBridgeCall {
        @JavascriptInterface
        @Keep
        public void call(String data) {
            android.util.Log.e("js", "data is " + data);
            PrivacyJsBridge.handle(data, iLoader);
        }
    }

}