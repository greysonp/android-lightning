package com.greysonparrelli.lightning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.greysonparrelli.lightning.R;
import com.greysonparrelli.lightning.model.ToolbarCommand;
import com.greysonparrelli.lightning.view.EditorWebView;

/**
 * @author greysonp
 */
public class ToolbarAdapter extends ArrayAdapter<ToolbarCommand> {

    private EditorWebView mWebView;

    public ToolbarAdapter(Context context, EditorWebView webView) {
        super(context, R.layout.item_toolbar_command);
        mWebView = webView;
        initCommands();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the view if we weren't given a convert view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_toolbar_command, null);
        }

        // Apply our model data to the view
        final ToolbarCommand toolbarCommand = getItem(position);
        ImageView button = (ImageView) convertView.findViewById(R.id.btn);
        button.setImageResource(toolbarCommand.getIconResource());

        // Link the command to trigger the appropriate action on the webview
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.sendCommand(toolbarCommand.getCommand());
            }
        });

        return convertView;
    }

    private void initCommands() {
        add(new ToolbarCommand(R.drawable.ic_command_underline, EditorWebView.Command.UNDERLINE));
        add(new ToolbarCommand(R.drawable.ic_command_italic, EditorWebView.Command.ITALIC));
        add(new ToolbarCommand(R.drawable.ic_command_bold, EditorWebView.Command.BOLD));
        add(new ToolbarCommand(R.drawable.ic_command_ordered_list, EditorWebView.Command.ORDERED_LIST));
        add(new ToolbarCommand(R.drawable.ic_command_unordered_list, EditorWebView.Command.UNORDERED_LIST));
    }
}
