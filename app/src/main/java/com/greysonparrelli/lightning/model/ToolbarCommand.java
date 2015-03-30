package com.greysonparrelli.lightning.model;

import com.greysonparrelli.lightning.view.EditorWebView;

/**
 * @author greysonp
 */
public class ToolbarCommand {

    private final int mIcon;
    private final EditorWebView.Command mCommand;

    public ToolbarCommand(int iconResource, EditorWebView.Command command) {
        mIcon = iconResource;
        mCommand = command;
    }

    public int getIconResource() {
        return mIcon;
    }

    public EditorWebView.Command getCommand() {
        return mCommand;
    }
}
