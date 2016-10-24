package sos.android.blesos.util;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import java.util.Locale;

import sos.android.blesos.R;
import sos.android.blesos.application.BaseApplication;
import sos.android.blesos.ui.activity.BaseActivity;

/**
 * Created by soorianarayanan on 16/10/16.
 */

public class Utils {
    private static final int notificationID = 1337;
    private static final int PERMISSION_LOCATION = 20;
    private static MediaPlayer sirenMediaPlayer;

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

    public static void createNotification(String title, String messageToShow, String piLink) {

        int requestID = (int) System.currentTimeMillis();
        NotificationManager notificationManager =
                (NotificationManager) BaseApplication.appContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Bitmap bm = BitmapFactory.decodeResource(BaseApplication.appContext.getResources(), R.mipmap.ic_launcher);

        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(piLink));

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(BaseApplication.appContext, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(BaseApplication.appContext)
                .setLargeIcon(bm)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageToShow))
                .setContentText(messageToShow)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(contentIntent)
                .setTicker(messageToShow)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.profile_username_icon);
        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    public static void openMap(String Googlelink) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Googlelink));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        BaseApplication.appContext.startActivity(intent);
    }

    public static void showRoute(String Latitude, String Longitude) {
        Location location = getLocation();
        double myLatitude = location.getLatitude();
        double myLongitude = location.getLongitude();
        if (Latitude != null && Longitude != null) {
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=" + myLatitude + "," + myLongitude + "&daddr=" + Latitude + "," + Longitude + "");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            BaseApplication.appContext.startActivity(intent);
        }
    }

    public static Location getLocation() {
        Location location;
        LocationManager locationManager = (LocationManager) BaseApplication.appContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(BaseApplication.appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BaseApplication.appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return TODO;

            ActivityCompat.requestPermissions(BaseActivity.activity, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_LOCATION);
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return location;
    }

    public static void playSirenSound() {
        if (sirenMediaPlayer == null) {
            sirenMediaPlayer = MediaPlayer.create(BaseApplication.appContext, R.raw.fps);
        }
        sirenMediaPlayer.start();
    }
}
