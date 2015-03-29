package com.greysonparrelli.lightning.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * @author greysonp
 */
public class EditorWebView extends WebView {
    public EditorWebView(Context context) {
        super(context);
        init();
    }

    public EditorWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void sendCommand(String command) {
        loadUrl("javascript:inputCommand('" + command + "')");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        getSettings().setJavaScriptEnabled(true);
        loadUrl("file:///android_asset/editor.html");
    }
}
