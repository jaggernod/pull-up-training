package com.jaggernod.pulluptraining;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Created by Pawel Polanski on 14/10/14.
 */
public class MyApplication extends Application {

    private static MyApplication singleton;

    public MyApplication getInstance(){
        return singleton;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
