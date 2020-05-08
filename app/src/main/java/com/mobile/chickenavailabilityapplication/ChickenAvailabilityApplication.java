package com.mobile.chickenavailabilityapplication;

import android.app.Application;
import android.content.Context;

/**
 * Created by harshareddy on 05/02/20.
 */

public class ChickenAvailabilityApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static Context getContext() {
        return mContext;
    }
}
