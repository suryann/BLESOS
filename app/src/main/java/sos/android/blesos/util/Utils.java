package sos.android.blesos.util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import sos.android.blesos.R;
import sos.android.blesos.application.BaseApplication;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by soorianarayanan on 16/10/16.
 */

public class Utils {
    private static final int notificationID = 1337;

    public static void showAlertDialog(final Context context, final String textToShow) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.app_name).setMessage(textToShow)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void createNotification(String title, String messageToShow) {

        NotificationManager notificationManager =
                (NotificationManager) BaseApplication.appContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Bitmap bm = BitmapFactory.decodeResource(BaseApplication.appContext.getResources(), R.mipmap.ic_launcher);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(BaseApplication.appContext)
                .setLargeIcon(bm)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageToShow))
                .setContentText(messageToShow)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
//                .setSubText(subtext)
                .setTicker(messageToShow)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.profile_username_icon);
        notificationManager.notify(notificationID, notificationBuilder.build());
    }

}
