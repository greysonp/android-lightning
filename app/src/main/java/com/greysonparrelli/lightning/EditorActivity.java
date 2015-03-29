package com.greysonparrelli.lightning;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import com.greysonparrelli.lightning.cloud.GoogleDrive;
import com.greysonparrelli.lightning.view.EditorWebView;


public class EditorActivity extends ActionBarActivity {

    private EditorWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        GoogleDrive.getInstance(this).connect(new GoogleDrive.IOnConnectedListener() {
            @Override
            public void onConnected() {
                GoogleDrive.getInstance(EditorActivity.this).getFileContents(new GoogleDrive.IOnContentsRetrievedListener() {
                    @Override
                    public void onContentsRetrieved(String contents) {
                        init(contents);
                    }
                });
            }
        });
    }

    private void init(String contents) {
        mWebView = (EditorWebView) findViewById(R.id.webview);

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
                mWebView.sendCommand(command);
            }
        });
    }
}
