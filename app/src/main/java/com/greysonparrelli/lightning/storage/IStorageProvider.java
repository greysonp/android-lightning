package com.greysonparrelli.lightning.storage;

import android.content.Intent;

/**
 * @author greysonp
 */
public interface IStorageProvider {

    void connect(IOnConnectedListener onConnectedListener);

    void saveFileContents(final String contents, final IOnFileSavedListener onFileSavedListener);

    void getFileContents(final IOnContentsRetrievedListener onContentRetrievedListener);

    void onActivityResult(final int requestCode, final int resultCode, final Intent data);

    interface IOnConnectedListener {
        void onConnected();
    }

    interface IOnContentsRetrievedListener {
        void onContentsRetrieved(String contents);
    }

    interface IOnFileSavedListener {
        void onFileSaved(boolean success);
    }
}
