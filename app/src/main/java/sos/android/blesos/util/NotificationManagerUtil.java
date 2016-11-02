package sos.android.blesos.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import sos.android.blesos.R;
import sos.android.blesos.application.BaseApplication;


/**
 * Utility class for android notification..
 *
 * @author soorianarayanan
 */
public class NotificationManagerUtil {

    private static final String TAG = NotificationManagerUtil.class.getSimpleName();
    private static NotificationManagerUtil notificationManagerUtil = null;
    private static Context mContext;

    private NotificationManagerUtil(Context context) {
        mContext = context;
    }

    /**
     * @return
     */
    public static NotificationManagerUtil getInstance() {
        if (notificationManagerUtil == null) {
            notificationManagerUtil = new NotificationManagerUtil(BaseApplication.appContext);
        }
        return notificationManagerUtil;
    }

    /**
     * @return
     */
    private NotificationManager getNotificationManager() {
        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager;
    }

    /**
     * add a vibration
     */
    public void setVibration() {
        try {
            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(5000);
        } catch (Exception e) {
            Log.d(TAG, "Error in vibration :" + e.getMessage().toString());
            e.printStackTrace();
        }
    }

    /**
     * cancel notification using unique id
     *
     * @param id
     */
    public void cancelNotifications(int id) {
        try {
            NotificationManager notificationManager = getNotificationManager();
            if (notificationManager != null) {
                notificationManager.cancel(id);
            }
        } catch (Exception e) {
            Log.d(TAG, "error in cancelActionNotifications " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * cancel all notifications
     */
    public void cancelAllNotification() {
        NotificationManager notificationManager = getNotificationManager();
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    /**
     * create a notifications
     *
     * @param context
     * @param pendingIntent
     */
    public void createNotifications(Context context,
                                    PendingIntent pendingIntent, String message) {
//        Uri soundUri = Uri.parse("android.resource://" + mContext.getPackageName() + "/"
//                + R.raw.notify_sound);

        if (TextUtils.isEmpty(message)) {
            return;
        }

        NotificationManager notificationManager = getNotificationManager();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(message).setOngoing(false).setDefaults(Notification.DEFAULT_LIGHTS)
                .setAutoCancel(true).setLights(0xff00ff00, 300, 1000)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        // Puts the PendingIntent into the notification builder
        mBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(100, mBuilder.build());
    }

}
