package sos.android.blesos.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import sos.android.blesos.db.DBManager;
import sos.android.blesos.util.SharedPreferenceUtil;

/**
 * Created by soorianarayanan on 13/10/16.
 */

public class BaseApplication extends Application {

    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;

        // Initialize Database
        DBManager.initDataBase(this);

        // Initialize a Shared Preference
        SharedPreferenceUtil.init(appContext);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getApplication() {
        return appContext;
    }
}
