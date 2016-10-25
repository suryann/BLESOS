package sos.android.blesos.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sos.android.blesos.R;
import sos.android.blesos.db.dao.NoSqlDao;
import sos.android.blesos.db.model.User;
import sos.android.blesos.sendmsg.SendMessage;
import sos.android.blesos.util.Constant;
import sos.android.blesos.util.Utils;

public class MainActivity extends BaseActivity implements View.OnClickListener, LocationListener {

    private ArrayList<User> users;
    private User user;
    private EditText nameAdd;
    private EditText mobileNumber;
    private Button addDetails;
    private LinearLayout mainActivityShowLayout;
    private static final int REQUEST_CODE_PERMISSION = 2;

    private LocationManager mgr;
    private Location location = null;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = null;

            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    message = "Message sent!";
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    message = "Error. Message not sent.";
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    message = "Error: No service.";
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    message = "Error: Null PDU.";
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    message = "Error: Radio off.";
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissionSMS();

        mainActivityShowLayout = (LinearLayout) findViewById(R.id.mainActivity_add_edit_layout);

        nameAdd = (EditText) findViewById(R.id.add_detail_name);
        mobileNumber = (EditText) findViewById(R.id.add_detail_mobileNumber);
        addDetails = (Button) findViewById(R.id.add_detail_addbutton);

        addDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = new User();
                String name = nameAdd.getText().toString();
                String mobile = mobileNumber.getText().toString();
                if (name.isEmpty() && mobile.isEmpty()) {
                    Toast.makeText(getBaseContext(), "name and mobile number should not be empty", Toast.LENGTH_LONG).show();
                } else if (users != null && users.size() > 4) {
                    Toast.makeText(getBaseContext(), "number of user limit is 5 only", Toast.LENGTH_LONG).show();
                } else {
                    user.setName(name);
                    user.setMobileNumber(mobile);
                    if (users == null)
                        users = new ArrayList<User>();
                    users.add(user);
                    NoSqlDao.getInstance().insertValues(Constant.user, users);
                    showUser();
                }
            }
        });

        registerReceiver(receiver, new IntentFilter(Constant.SMS_SENT));  // SMS_SENT is a constant

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                sendSms();
                Toast.makeText(getBaseContext(), "User Location has been send through sms", Toast.LENGTH_LONG).show();
            }
        });

        users = (ArrayList<User>) NoSqlDao.getInstance().findSerializeData(Constant.user);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent BaseActivity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

    private void showUser() {
        View v;
        mainActivityShowLayout.removeAllViews();
        if (users != null && !users.isEmpty()) {
            for (final User user : users) {
                String userName = user.getName();
                String userMobileNumber = user.getMobileNumber();
                v = getLayoutInflater().inflate(R.layout.view_detail, null);
                TextView name = (TextView) v.findViewById(R.id.view_detail_name);
                TextView mobileNumber = (TextView) v.findViewById(R.id.view_detail_mobile_number);
                final Button delete = (Button) v.findViewById(R.id.view_detail_delete);
                mainActivityShowLayout.addView(v);

                name.setText(userName);
                mobileNumber.setText(userMobileNumber);

                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        delete.setVisibility(View.VISIBLE);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                users.remove(user);
                                NoSqlDao.getInstance().insertValues(Constant.user, users);
                                showUser();
                            }
                        });
                        return false;
                    }
                });
            }
        }
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

    public Location getLocation() {

        String best;
        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        best = mgr.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }

        mgr.requestLocationUpdates(best, 1500, 1, this);
        if (location == null) {
            location = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null)
                location = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location == null) {
                location = Utils.getLocation();
            }
        }
        Log.v("", "Latitude  :		" + location.getLatitude() + "\n");
        Log.v("", "Langitude :		" + location.getLongitude() + "\n");

        return location;
    }

    public void checkPermissionSMS() {

        String[] mPermission = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, mPermission,
                    REQUEST_CODE_PERMISSION);
        } else {
            // TODO
        }
    }

    private void sendSms() {
        String message;
        ArrayList<String> receipientList = new ArrayList<>();
        message = "key : " + getResources().getString(R.string.key)
                + " ; GoogleLink for Map : " + Constant.GOOGLELINK + location.getLatitude() + "," + location.getLongitude()
                + " ; Latitude : " + (int) (location.getLatitude() * 1E6)
                + " ; Longitude : " + (int) (location.getLongitude() * 1E6)
                + " ; Address : " + getAddress(location);

        if (users != null)
            for (User user : users) {
                receipientList.add(user.getMobileNumber());
            }
        new SendMessage(receipientList, message);
    }

    private String getAddress(Location location) {
        String address = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            for (int j = 0; j <= addresses.size() - 1; ) {
                Address firstadd = addresses.get(j);
                for (int i = 0; i <= firstadd.getMaxAddressLineIndex(); ) {
                    address = address + firstadd.getAddressLine(i).toString();
//    			   log("       "+firstadd.getAddressLine(i)+"\n");
                    i++;
                }
                j++;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return address;
    }
}
