package com.greysonparrelli.lightning.cloud;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * @author greyson
 */
public class GoogleDrive implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "GoogleDrive";
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 24601;
    private static final String FILE_NAME = "lightning.html";
    private static final String KEY_FILE_ID = "file_id";

    private static GoogleDrive sInstance;

    private Activity mActivity;
    private GoogleApiClient mClient;
    private IOnConnectedListener mOnConnectedListener;


    // =============================================
    // Overrides
    // =============================================

    @Override
    public void onConnected(Bundle bundle) {
        if (mOnConnectedListener != null) {
            mOnConnectedListener.onConnected();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(mActivity, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), mActivity, 0).show();
        }
    }


    // =============================================
    // Public
    // =============================================

    public static GoogleDrive getInstance(Activity activity) {
        if (sInstance == null) {
            synchronized (GoogleDrive.class) {
                if (sInstance == null) {
                    sInstance = new GoogleDrive(activity);
                }
            }
        }

        return sInstance;
    }

    public void connect(IOnConnectedListener onConnectedListener) {
        mOnConnectedListener = onConnectedListener;
        if (mClient.isConnected() && mOnConnectedListener != null) {
            mOnConnectedListener.onConnected();
        } else {
            mClient.connect();
        }
    }

    public void saveFileContents(final String contents, final IOnFileSavedListener onFileSavedListener) {
        findOrCreateNewFile(new IOnFileCreatedListener() {
            @Override
            public void onFileCreated(DriveFile file) {
                if (file != null) {
                    updateFileContents(file, contents, onFileSavedListener);
                } else {
                    onFileSavedListener.onFileSaved(false);
                }
            }
        });
    }

    public void getFileContents(final IOnContentsRetrievedListener onContentRetrievedListener) {
        findOrCreateNewFile(new IOnFileCreatedListener() {
            @Override
            public void onFileCreated(DriveFile file) {
                if (file != null) {
                    readFileContents(file, onContentRetrievedListener);
                } else {
                    onContentRetrievedListener.onContentsRetrieved("");
                }
            }
        });
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    mClient.connect();
                }
                break;
        }
    }


    // =============================================
    // Private
    // =============================================

    private GoogleDrive(Activity appContext) {
        mActivity = appContext;
        mClient = new GoogleApiClient.Builder(mActivity)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void findOrCreateNewFile(final IOnFileCreatedListener onFileCreatedListener) {
        queryForFile(new IOnFileFoundListener() {
            @Override
            public void onFileFound(@Nullable DriveId id) {
                if (id != null) {
                    onFileCreatedListener.onFileCreated(Drive.DriveApi.getFile(mClient, id));
                } else {
                    createNewFile(onFileCreatedListener);
                }
            }
        });
    }

    private void createNewFile(final IOnFileCreatedListener onFileCreatedListener) {
        Log.d(TAG, "Creating new file.");
        // Create a new file
        Drive.DriveApi.newDriveContents(mClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult result) {
                if (!result.getStatus().isSuccess()) {
                    Log.e(TAG, "Error while trying to create new file contents");
                    return;
                }

                // Build the file metadata
                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle(FILE_NAME)
                        .setMimeType("text/html")
                        .build();

                // Save the file contents
                Drive.DriveApi.getAppFolder(mClient)
                        .createFile(mClient, changeSet, result.getDriveContents())
                        .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                            @Override
                            public void onResult(DriveFolder.DriveFileResult fileResult) {
                                if (!fileResult.getStatus().isSuccess()) {
                                    Log.e(TAG, "Error while trying to create the file");
                                    onFileCreatedListener.onFileCreated(null);
                                    return;
                                }
                                onFileCreatedListener.onFileCreated(fileResult.getDriveFile());
                            }
                        });
            }
        });
    }

    private void updateFileContents(DriveFile driveFile, final String contents, final IOnFileSavedListener onFileSavedListener) {
        new GoogleClientAsyncTask<DriveFile, Void, Boolean>(mActivity) {
            @Override
            protected Boolean doInBackgroundConnected(DriveFile... params) {
                DriveFile file = params[0];
                try {
                    DriveApi.DriveContentsResult driveContentsResult = file.open(
                            getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, new DownloadListener()).await();
                    if (!driveContentsResult.getStatus().isSuccess()) {
                        return false;
                    }
                    DriveContents driveContents = driveContentsResult.getDriveContents();
                    OutputStream outputStream = driveContents.getOutputStream();
                    outputStream.write(contents.getBytes());
                    com.google.android.gms.common.api.Status status =
                            driveContents.commit(getGoogleApiClient(), null).await();
                    return status.getStatus().isSuccess();
                } catch (IOException e) {
                    Log.e(TAG, "IOException while appending to the output stream", e);
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                result = result == null ? false : result;
                if (!result) {
                    Log.e(TAG, "Error while editing contents");
                }
                if (onFileSavedListener != null) {
                    onFileSavedListener.onFileSaved(result);
                }
            }
        }.execute(driveFile);
    }

    private void queryForFile(final IOnFileFoundListener onFileFoundListener) {
        // First check if the file exists already
        Query query = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, FILE_NAME)).build();
        Drive.DriveApi.getAppFolder(mClient).queryChildren(mClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult result) {
                if (!result.getStatus().isSuccess()) {
                    Log.e(TAG, "Problem while retrieving results");
                    onFileFoundListener.onFileFound(null);
                    return;
                }
                MetadataBuffer buffer = result.getMetadataBuffer();
                Iterator<Metadata> iterator = result.getMetadataBuffer().iterator();
                if (iterator.hasNext()) {
                    onFileFoundListener.onFileFound(iterator.next().getDriveId());
                } else {
                    onFileFoundListener.onFileFound(null);
                }
                buffer.release();
            }
        });
    }

    private void readFileContents(DriveFile file, final IOnContentsRetrievedListener onContentsReadListener) {
        new GoogleClientAsyncTask<DriveFile, Void, String>(mActivity) {

            @Override
            protected String doInBackgroundConnected(DriveFile... params) {
                DriveFile file = params[0];
                try {
                    DriveApi.DriveContentsResult driveContentsResult = file.open(
                            getGoogleApiClient(), DriveFile.MODE_READ_ONLY, new DownloadListener()).await();
                    if (!driveContentsResult.getStatus().isSuccess()) {
                        return null;
                    }
                    DriveContents driveContents = driveContentsResult.getDriveContents();
                    BufferedReader inputStream = new BufferedReader(new InputStreamReader(driveContents.getInputStream()));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = inputStream.readLine()) != null) {
                        builder.append(line);
                    }
                    return builder.toString();
                } catch (IOException e) {
                    Log.e(TAG, "IOException while appending to the output stream", e);
                }

                return null;
            }

            @Override
            protected void onPostExecute(String contents) {
                if (onContentsReadListener != null) {
                    onContentsReadListener.onContentsRetrieved(contents);
                }

            }
        }.execute(file);
    }


    // =============================================
    // Classes
    // =============================================

    public interface IOnConnectedListener {
        void onConnected();
    }

    public interface IOnContentsRetrievedListener {
        void onContentsRetrieved(String contents);
    }

    public interface IOnFileSavedListener {
        void onFileSaved(boolean success);
    }

    private interface IOnFileCreatedListener {
        void onFileCreated(@Nullable DriveFile file);
    }

    private interface IOnFileFoundListener {
        void onFileFound(@Nullable DriveId id);
    }

    private class DownloadListener implements DriveFile.DownloadProgressListener {

        private ProgressDialog mProgressDialog;

        @Override
        public void onProgress(long bytesDownloaded, long bytesExpected) {
            // bytesExpected will be negative if there's nothing to download
            if (bytesExpected <= 0) {
                return;
            }

            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(mActivity, ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.show();
            }
            int percent = (int) Math.round((bytesDownloaded * 1.0) / (bytesExpected * 1.0) * 100);
            Log.d("REMOVE", "downloaded: " + bytesDownloaded + " | expected: " + bytesExpected + " | percent: " + percent);

            mProgressDialog.setProgress(percent);

            if (percent >= 100) {
                mProgressDialog.dismiss();
            }
        }
    }
}
