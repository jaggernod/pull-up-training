package com.jaggernod.pulluptraining;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

/**
 * Created by Pawel Polanski on 14/10/14.
 */
public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();

    private static MyApplication singleton;

    public MyApplication getInstance(){
        return singleton;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "Configuration has changed");
        StrictModeHelper.activityRecreation();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        StrictModeHelper.startStrictMode();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "Low memory warning");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.w(TAG, "Terminate application request");
    }

}
