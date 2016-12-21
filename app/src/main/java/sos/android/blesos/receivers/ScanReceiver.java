package sos.android.blesos.receivers;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sos.android.blesos.R;
import sos.android.blesos.application.BaseApplication;
import sos.android.blesos.ble.BLEConnection;
import sos.android.blesos.db.dao.NoSqlDao;
import sos.android.blesos.db.model.User;
import sos.android.blesos.sendmsg.SendMessage;
import sos.android.blesos.ui.activity.BaseActivity;
import sos.android.blesos.util.Constant;
import sos.android.blesos.util.SharedPreferenceUtil;
import sos.android.blesos.util.Utility;
import sos.android.blesos.util.Utils;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ScanReceiver extends BroadcastReceiver {
    private static final int PERMISSION_CALL = 10;
    private String address;
    private BLEConnection bleConnection;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private Context context;
    private ScanCallback mScanCallback;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 40000;
    public static final String TAG = ScanReceiver.class.getName();
    public static boolean msgFlag = true;
    public static boolean bluetoothState = true;

    /**
     * BLE scan callback below 21
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    if (device.getAddress().equals(SharedPreferenceUtil.getInstance().getStringValue(SharedPreferenceUtil.MAC_ADD, ""))) {
                        Location location = getLocation();

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                msgFlag = true;
                            }
                        };

                        //location is not mandatory
//                        if (location != null) {
                            mHandler.postDelayed(runnable, 1000 * 40);
                            if (msgFlag) {
                                msgFlag = false;
                                sendSms(location);
                                Log.v(TAG, "msg send from receiver");
//                            }
                        }
                    }
                }
            };

    public ScanReceiver() {

        /**
         * BLE scan callback above 21
         */
        mHandler = new Handler();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mScanCallback = new ScanCallback() {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        msgFlag = true;
                    }
                };

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    if (Constant.DEBUG) {
                        Log.d("callbackType", String.valueOf(callbackType));
                        Log.d("result", result.toString());
                    }
                    final BluetoothDevice btDevice = result.getDevice();
                    if (btDevice.getAddress().equals(SharedPreferenceUtil.getInstance().getStringValue(SharedPreferenceUtil.MAC_ADD, ""))) {
                        Location location = getLocation();
                        //location is not mandatory
//                        if (location != null) {
                            mHandler.postDelayed(runnable, 1000 * 40);
                            if (msgFlag) {
                                msgFlag = false;
                                sendSms(location);
                                Utility.showToast("msg send from receiver");
                                Log.v(TAG, "msg send from receiver");
                            }
//                        }
                    }
                }

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    for (ScanResult sr : results) {
                        if (Constant.DEBUG) {
                            Log.i("ScanResult - Results", sr.toString());
                        }
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    if (Constant.DEBUG) {
                        Log.e("Scan Failed", "Error Code: " + errorCode);
                    }
                }
            };
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        this.context = context;


        address = SharedPreferenceUtil.getInstance().getStringValue(SharedPreferenceUtil.MAC_ADD, null);
        if (address != null) {
            if (bleConnection == null){
                bleConnection = BLEConnection.getInstance();
            }
                // After ble connection
//            if (bleConnection.isConnected(address)) {
//
//            } else {
//
//            }

            scanLeDevice(true);

        } else {

        }
    }

    public Location getLocation() {

        String best;
        LocationManager mgr;
        mgr = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        best = mgr.getBestProvider(criteria, true);
        final Location[] location = new Location[1];

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }

        mgr.requestLocationUpdates(best, 1500, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location locationLis) {
                location[0] = locationLis;
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
        });
        if (location[0] == null) {
            location[0] = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location[0] == null)
                location[0] = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location[0] == null) {
                location[0] = Utils.getLocation(BaseActivity.activity);
            }
            Log.v("", "Latitude  :		" + location[0].getLatitude() + "\n");
            Log.v("", "Langitude :		" + location[0].getLongitude() + "\n");
        }
        return location[0];
    }

    private void sendSms(Location location) {
        String message;
        ArrayList<User> users = (ArrayList<User>) NoSqlDao.getInstance().findSerializeData(Constant.user);

        ArrayList<String> receipientList = new ArrayList<>();

        String key = SharedPreferenceUtil.getInstance().getStringValue(SharedPreferenceUtil.CUSTOM_SMS_KEY, "");

        if (key.isEmpty())
            key = "key : ";
        else
            key = key + " : ";

        if (location == null) {
            message = key + BaseApplication.appContext.getResources().getString(R.string.key)
                    + "Need Help";
        } else {
            message = key + context.getResources().getString(R.string.key)
                    + " ; GoogleLink for Map : " + Constant.GOOGLELINK + location.getLatitude() + "," + location.getLongitude()
                    + " ; Latitude : " + (int) (location.getLatitude() * 1E6)
                    + " ; Longitude : " + (int) (location.getLongitude() * 1E6)
                    + " ; Address : " + getAddress(context, location);
        }

        if (users != null)
            for (User user : users) {
                receipientList.add(user.getMobileNumber());
            }
        new SendMessage(receipientList, message);
    }

    private String getAddress(Context cotext, Location location) {
        String address = null;
        Geocoder geocoder = new Geocoder(cotext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            for (int j = 0; j <= addresses.size() - 1; ) {
                Address firstadd = addresses.get(j);
                for (int i = 0; i <= firstadd.getMaxAddressLineIndex(); ) {
                    address = address + firstadd.getAddressLine(i).toString();
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

    /**
     * start scanning the BLE devices
     *
     * @param enable - ENABLE/DISABLE the BLE scan
     */
    private void scanLeDevice(final boolean enable) {

        //Get bluetooth mScanAdapter
        BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(context.BLUETOOTH_SERVICE);
        final BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();


        if (Build.VERSION.SDK_INT >= 21) {
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<ScanFilter>();
        }

        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        if (mBluetoothAdapter != null)
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        if (mLEScanner != null && mBluetoothAdapter.isEnabled() && mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON && bluetoothState)
                            mLEScanner.stopScan(mScanCallback);
                    }
                }
            }, SCAN_PERIOD);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                if (mBluetoothAdapter != null)
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                if (mLEScanner != null && mBluetoothAdapter.isEnabled() && mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON && bluetoothState)
                    mLEScanner.startScan(filters, settings, mScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                if (mBluetoothAdapter != null)
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                if (mLEScanner != null && mBluetoothAdapter.isEnabled() && mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON && bluetoothState)
                    mLEScanner.stopScan(mScanCallback);
            }
        }
    }
}
