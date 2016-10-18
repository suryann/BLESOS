package sos.android.blesos.util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import sos.android.blesos.R;
import sos.android.blesos.application.BaseApplication;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by soorianarayanan on 16/10/16.
 */

public class Utils {

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

}
