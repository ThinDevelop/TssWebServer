package com.tss.webserver;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.tss.webserver.entity.Cache;

import java.util.Locale;

public class AllinpaySYBApplication extends MultiDexApplication {

    //public static AidlDeviceService mDeviceService;
    public static AidlDeviceManager mDeviceManager;
    //PBOC监听
    public static PbocListener mPbocListener;

    @Override
    public void onCreate() {
        super.onCreate();
        Locale.setDefault(Locale.ENGLISH);
        Cache.getInstance().setContext(this.getApplicationContext());
//		SQLiteDatabase.loadLibs(this);
        mPbocListener = new PbocListener();
        registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.i("onActivityResumed:", activity.getClass().getName());
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

}
