package com.nosuchserver.application;

import android.app.Application;

/**
 * Created by rere on 18-5-15.
 */

public class OurApplication extends Application {

    private static OurApplication sInstance;

    public static OurApplication getInstance() {
        if (sInstance == null) {
            throw new RuntimeException("IlleagelStateExp : instance is null, application error");
        }
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
