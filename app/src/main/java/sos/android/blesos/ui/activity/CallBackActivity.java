package sos.android.blesos.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import sos.android.blesos.R;
import sos.android.blesos.util.Constant;
import sos.android.blesos.util.Utils;

import static sos.android.blesos.util.Utils.getLocation;

public class CallBackActivity extends AppCompatActivity implements LocationListener {

    private static final int PERMISSION_CALL = 10;
    private static final String TAG = CallBackActivity.class.getName();
    private Intent receiveIntent;
    private String latitude;
    private String longitude;
    private String phoneNumber;

    private TextView phoneNumberTxt;
    private Button callButton;
    private Location location;
    private LocationManager mgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_back);
        receiveIntent = getIntent();

        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (location == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null)
                location = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location == null) {
                location = Utils.getLocation(this);
            }
        }

        latitude = receiveIntent.getStringExtra(Constant.LATITUDE);
        longitude = receiveIntent.getStringExtra(Constant.LONGITUDE);
        phoneNumber = receiveIntent.getStringExtra(Constant.PHONENUMBER);

        phoneNumberTxt = (TextView) findViewById(R.id.activity_call_back_call_text);
        phoneNumberTxt.append(phoneNumber);

        callButton = (Button) findViewById(R.id.activity_call_back_call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(phoneNumber);
            }
        });
        showRoute();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void call(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(getString(R.string.tel) + Uri.encode(phoneNumber.trim())));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(BaseActivity.activity, new String[]{
                            Manifest.permission.CALL_PHONE,},
                    PERMISSION_CALL);
            return;
        }
        startActivity(callIntent);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void route() {
        Log.v(TAG, "show route ");
        double myLatitude = location.getLatitude();
        double myLongitude = location.getLongitude();
        String uri = String.format(Locale.ENGLISH, getString(R.string.maplink_sadd) + myLatitude + "," + myLongitude + getString(R.string.desti_add) + latitude + "," + longitude + "");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName(getString(R.string.google_class_name), getString(R.string.google_class_nametwo));
        startActivity(intent);
    }

    private void showRoute() {
        if (location == null) {
            location = getLocation(this);
            Log.v(TAG, "location is null");
        } else
            route();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v(TAG, "onRequestPermissionsResult ");
        route();
    }
}
