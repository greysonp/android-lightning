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

    public void sendCommand(Command command) {
        loadUrl("javascript:inputCommand('" + command.getCommand() + "')");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        getSettings().setJavaScriptEnabled(true);
        loadUrl("file:///android_asset/editor.html");
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
