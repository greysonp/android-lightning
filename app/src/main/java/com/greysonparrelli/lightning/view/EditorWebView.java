package com.greysonparrelli.lightning.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * @author greysonp
 */
public class EditorWebView extends WebView {

    private IOnContentRetrievedListener mOnContentRetrievedListener;
    private IOnEditorEventListener mOnEditorEventListener;
    private boolean mLoadedWithoutListener;

    public EditorWebView(Context context) {
        super(context);
        init();
    }

    public EditorWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void sendCommand(Command command) {
        loadUrl("javascript:inputCommand('" + command.getCommand() + "')");
    }

    public void setContent(String content) {
        loadUrl("javascript:setContent('" + content + "')");
    }

    public void getContent(IOnContentRetrievedListener listener) {
        mOnContentRetrievedListener = listener;
        loadUrl("javascript:getContent()");
    }

    public void setOnEditorEventListener(IOnEditorEventListener listener) {
        mOnEditorEventListener = listener;
        if (mLoadedWithoutListener) {
            mOnEditorEventListener.onReady();
            mLoadedWithoutListener = false;
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void init() {
        WebView.setWebContentsDebuggingEnabled(true);
        getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(new EditorJavascriptInterface(), "Android");
        loadUrl("file:///android_asset/editor.html");
    }

    public class EditorJavascriptInterface {

        @JavascriptInterface
        public void sendContent(String content) {
            if (mOnContentRetrievedListener != null) {
                mOnContentRetrievedListener.onContentRetrieved(content);
            }
        }

        @JavascriptInterface
        public void saveContent(String content) {
            if (mOnEditorEventListener != null) {
                mOnEditorEventListener.onContentShouldBeSaved(content);
            }
        }

        @JavascriptInterface
        public void setNotSynced() {
            if (mOnEditorEventListener != null) {
                mOnEditorEventListener.onNotSynced();
            }
        }

        @JavascriptInterface
        public void onReady() {
            if (mOnEditorEventListener != null) {
                mOnEditorEventListener.onReady();
            } else {
                mLoadedWithoutListener = true;
            }
        }
    }

    public interface IOnContentRetrievedListener {
        void onContentRetrieved(String content);
    }

    public interface IOnEditorEventListener {
        void onReady();
        void onContentShouldBeSaved(String content);
        void onNotSynced();
    }

    public enum Command {
        BOLD("bold"),
        ITALIC("italic"),
        UNDERLINE("underline"),
        ORDERED_LIST("insertOrderedList"),
        UNORDERED_LIST("insertUnorderedList"),
        INDENT("indent"),
        OUTDENT("outdent");

        private String mCommand;

        Command(String command) {
            mCommand = command;
        }

        public String getCommand() {
            return mCommand;
        }
    }
}
