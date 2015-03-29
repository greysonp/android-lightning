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
        mWebView.setContent(contents);

        linkButtonToCommand(R.id.btn_bold, EditorWebView.Command.BOLD);
        linkButtonToCommand(R.id.btn_italic, EditorWebView.Command.ITALIC);
        linkButtonToCommand(R.id.btn_underline, EditorWebView.Command.UNDERLINE);
        linkButtonToCommand(R.id.btn_ol, EditorWebView.Command.ORDERED_LIST);
        linkButtonToCommand(R.id.btn_ul, EditorWebView.Command.UNORDERED_LIST);
        linkButtonToCommand(R.id.btn_indent, EditorWebView.Command.INDENT);
        linkButtonToCommand(R.id.btn_outdent, EditorWebView.Command.OUTDENT);
    }

    private void linkButtonToCommand(int btnId, final EditorWebView.Command command) {
        View btn = findViewById(btnId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.sendCommand(command);
                mWebView.getContent(new EditorWebView.IOnContentRetrievedListener() {
                    @Override
                    public void onContentRetrieved(String content) {
                        GoogleDrive.getInstance(EditorActivity.this).saveFileContents(content, null);
                    }
                });
            }
        });
    }
}
