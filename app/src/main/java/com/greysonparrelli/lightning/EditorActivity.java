package com.greysonparrelli.lightning;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.greysonparrelli.lightning.adapter.ToolbarAdapter;
import com.greysonparrelli.lightning.cloud.GoogleDrive;
import com.greysonparrelli.lightning.view.EditorWebView;


public class EditorActivity extends Activity {

    private static final String TAG = "EditorActivity";

    private EditorWebView mWebView;
    private ListView mToolbarSide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        initLayout();

        GoogleDrive.getInstance(this).connect(new GoogleDrive.IOnConnectedListener() {
            @Override
            public void onConnected() {
                GoogleDrive.getInstance(EditorActivity.this).getFileContents(new GoogleDrive.IOnContentsRetrievedListener() {
                    @Override
                    public void onContentsRetrieved(String contents) {
                        initEditorWithContents(contents);
                    }
                });
            }
        });
    }

    private void initLayout() {
        mWebView = (EditorWebView) findViewById(R.id.webview);

        mToolbarSide = (ListView) findViewById(R.id.toolbar_side);
        mToolbarSide.setAdapter(new ToolbarAdapter(this, mWebView));

    }

    private void initEditorWithContents(String contents) {
        mWebView.setContent(contents);
        mWebView.setOnEditorEventListener(new EditorEventListener());

        linkButtonToCommand(R.id.btn_indent, EditorWebView.Command.INDENT);
        linkButtonToCommand(R.id.btn_outdent, EditorWebView.Command.OUTDENT);
    }

    private void linkButtonToCommand(int btnId, final EditorWebView.Command command) {
        View btn = findViewById(btnId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.sendCommand(command);
            }
        });
    }

    private class EditorEventListener implements EditorWebView.IOnEditorEventListener {

        @Override
        public void onContentShouldBeSaved(String content) {
            Log.d(TAG, "Saving content...");
            GoogleDrive.getInstance(EditorActivity.this).saveFileContents(content, new GoogleDrive.IOnFileSavedListener() {
                @Override
                public void onFileSaved(boolean success) {
                    if (success) {
                        Log.d(TAG, "Content saved.");
                    } else {
                        Log.w(TAG, "Content failed to save.");
                    }
                }
            });
        }

        @Override
        public void onNotSynced() {
        }
    }
}
