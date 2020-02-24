package com.tss.webserver.utils;

import android.content.SharedPreferences;

import com.tss.webserver.configulations.TssSharedPreferences;

import java.util.UUID;

public class Util {

    private static final String KEY_UUID = "KEY_UUID";

    public static String getUUID() {
        SharedPreferences sharedPreferences = TssSharedPreferences.getSharedpreferences();
        String uuid = sharedPreferences.getString(KEY_UUID, "");
        if (uuid.isEmpty()) {
            uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = TssSharedPreferences.getSharedpreferences().edit();
            editor.putString(KEY_UUID, uuid);
            editor.apply();
        }
        return uuid;
    }
}
