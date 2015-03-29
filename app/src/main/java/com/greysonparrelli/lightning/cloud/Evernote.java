package com.greysonparrelli.lightning.cloud;

import android.content.Context;

import com.evernote.client.android.EvernoteSession;
import com.greysonparrelli.lightning.R;

/**
 * @author greyson
 */
public class Evernote {

    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;

    private final String CONSUMER_KEY;
    private final String CONSUMER_SECRET;

    private static Evernote sInstance;
    private static EvernoteSession sSession;

    private Context mAppContext;


    // =============================================
    // Public
    // =============================================

    public static Evernote getInstance(Context appContext) {
        if (sInstance == null) {
            synchronized (Evernote.class) {
                if (sInstance == null) {
                    sInstance = new Evernote(appContext);
                }
            }
        }

        return sInstance;
    }

    public void authenticate() {
        getSession().authenticate(mAppContext);
    }

    public boolean isLoggedIn() {
        return getSession().isLoggedIn();
    }


    // =============================================
    // Private
    // =============================================

    private Evernote(Context appContext) {
        mAppContext = appContext.getApplicationContext();
        CONSUMER_KEY = mAppContext.getString(R.string.evernote_consumer_key);
        CONSUMER_SECRET = mAppContext.getString(R.string.evernote_consumer_secret);
    }

    private EvernoteSession getSession() {
        if (sSession == null) {
            sSession = EvernoteSession.getInstance(mAppContext, CONSUMER_KEY, CONSUMER_SECRET, EVERNOTE_SERVICE, false);
        }
        return sSession;
    }
}
