package com.greysonparrelli.lightning;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.greysonparrelli.lightning.R;


public class EditorActivity extends ActionBarActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///android_asset/editor.html");

        linkButtonToCommand(R.id.btn_bold, "bold");
        linkButtonToCommand(R.id.btn_italic, "italic");
        linkButtonToCommand(R.id.btn_underline, "underline");
        linkButtonToCommand(R.id.btn_ol, "insertOrderedList");
        linkButtonToCommand(R.id.btn_ul, "insertUnorderedList");
        linkButtonToCommand(R.id.btn_indent, "indent");
        linkButtonToCommand(R.id.btn_outdent, "outdent");
    }

    private void linkButtonToCommand(int btnId, final String command) {
        View btn = findViewById(btnId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:inputCommand('" + command + "')");
            }
        });
    }
}
