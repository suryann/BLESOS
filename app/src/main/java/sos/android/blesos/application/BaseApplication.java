package sos.android.blesos.application;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.util.Log;

import sos.android.blesos.db.DBManager;
import sos.android.blesos.receivers.ScanReceiver;
import sos.android.blesos.util.SharedPreferenceUtil;

/**
 * Created by Radso Technologies on 13/10/16.
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

    public static void setAlarm(Context context) {
        Log.i("setAlarm", " Alarm set");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ScanReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60, pi);
    }

    public static void cancelAlarm(Context context) {
        Log.i("cancelAlarm", " Alarm Camceled");
        Intent intent = new Intent(context, ScanReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

}
