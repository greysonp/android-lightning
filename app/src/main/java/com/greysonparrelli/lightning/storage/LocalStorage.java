package com.greysonparrelli.lightning.storage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author greysonp
 */
public class LocalStorage implements IStorageProvider {

    private static final String TAG = "LocalStorage";
    private static final String FILE_NAME = "lightning.html";

    private static LocalStorage sInstance;
    private Context mContext;


    // =============================================
    // Overrides
    // =============================================

    @Override
    public void connect(IOnConnectedListener onConnectedListener) {
        if (onConnectedListener != null) {
            onConnectedListener.onConnected();
        }
    }

    @Override
    public void saveFileContents(final String contents, final IOnFileSavedListener onFileSavedListener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                // Save file
                FileOutputStream outputStream = null;
                try {
                    outputStream = mContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                    outputStream.write(contents.getBytes());

                } catch (FileNotFoundException e) {
                    Log.e(TAG, "Could not find file to write to.", e);
                    return false;
                } catch (IOException e) {
                    Log.e(TAG, "Error while writing file");
                    return false;

                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (onFileSavedListener != null) {
                    onFileSavedListener.onFileSaved(success);
                }
            }
        }.execute();
    }

    @Override
    public void getFileContents(final IOnContentsRetrievedListener onContentRetrievedListener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                // Read file
                StringBuilder builder = new StringBuilder();
                BufferedReader inputStream = null;
                try {
                    inputStream = new BufferedReader(new InputStreamReader(mContext.openFileInput(FILE_NAME)));
                    String line;
                    while ((line = inputStream.readLine()) != null) {
                        builder.append(line);
                    }
                } catch (FileNotFoundException e) {
                    Log.w(TAG, "Could not find file while reading. This is probably just the first time we're trying to read. No worries.", e);
                    return "";
                } catch (IOException e) {
                    Log.e(TAG, "Error while writing file.", e);
                    return "";
                }
                return builder.toString();
            }

            @Override
            protected void onPostExecute(String content) {
                if (onContentRetrievedListener != null) {
                    onContentRetrievedListener.onContentsRetrieved(content);
                }
            }
        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Don't need to do anything
    }


    // =============================================
    // Public
    // =============================================

    public static LocalStorage getInstance(Context appContext) {
        if (sInstance == null) {
            synchronized (LocalStorage.class) {
                if (sInstance == null) {
                    sInstance = new LocalStorage(appContext);
                }
            }
        }

        return sInstance;
    }

    // =============================================
    // Private
    // =============================================

    private LocalStorage(Context appContext) {
        mContext = appContext;
    }

}
