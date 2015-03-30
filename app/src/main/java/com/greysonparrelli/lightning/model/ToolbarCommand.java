package com.greysonparrelli.lightning.model;

import com.greysonparrelli.lightning.view.EditorWebView;

/**
 * @author greysonp
 */
public class ToolbarCommand {

    private final String mTitle;
    private final EditorWebView.Command mCommand;

    public ToolbarCommand(String title, EditorWebView.Command command) {
        mTitle = title;
        mCommand = command;
    }

    public String getTitle() {
        return mTitle;
    }

    public EditorWebView.Command getCommand() {
        return mCommand;
    }
}
