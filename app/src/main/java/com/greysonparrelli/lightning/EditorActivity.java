package com.greysonparrelli.lightning;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.greysonparrelli.lightning.adapter.ToolbarAdapter;
import com.greysonparrelli.lightning.storage.IStorageProvider;
import com.greysonparrelli.lightning.storage.LocalStorage;
import com.greysonparrelli.lightning.view.EditorWebView;


public class EditorActivity extends Activity {

    private static final String TAG = "EditorActivity";

    private EditorWebView mWebView;
    private ListView mToolbarSide;
    private IStorageProvider mStorageProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        initLayout();

        mStorageProvider = LocalStorage.getInstance(getApplicationContext());

        mStorageProvider.connect(new IStorageProvider.IOnConnectedListener() {
            @Override
            public void onConnected() {
                Log.d(TAG, "Loading content...");
                mStorageProvider.getFileContents(new IStorageProvider.IOnContentsRetrievedListener() {
                    @Override
                    public void onContentsRetrieved(String contents) {
                        Log.d(TAG, "Content retrieved.");
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
            mStorageProvider.saveFileContents(content, new IStorageProvider.IOnFileSavedListener() {
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
