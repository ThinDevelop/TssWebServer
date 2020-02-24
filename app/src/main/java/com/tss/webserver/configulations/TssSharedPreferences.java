package com.tss.webserver.configulations;

import android.content.Context;
import android.content.SharedPreferences;

import app.TssServerApplication;

public class TssSharedPreferences {
    private static final String tssPreference = "TSS_SHARED_PREFERENCES";
    private static SharedPreferences sharedpreferences = null;

    public static SharedPreferences getSharedpreferences() {
        if (sharedpreferences == null) {
            sharedpreferences = TssServerApplication.Companion.getAppContext().getSharedPreferences(tssPreference, Context.MODE_PRIVATE);
        }
        return sharedpreferences;
    }
}
