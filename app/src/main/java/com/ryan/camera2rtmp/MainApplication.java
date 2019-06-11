package com.ryan.camera2rtmp;

import android.app.Activity;
import android.app.Application;

public class MainApplication extends Application {

    private static MainApplication INSTANCE;

    private static Activity CURRENT_ACTIVITY;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    public static MainApplication getInstance() {
        return INSTANCE;
    }

    public static void setCurrentActivity(Activity activity) {
        CURRENT_ACTIVITY = activity;
    }

    public static Activity getCurrentActivity() {
        return CURRENT_ACTIVITY;
    }


}
