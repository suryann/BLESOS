package sos.android.blesos.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sos.android.blesos.R;
import sos.android.blesos.adapter.ScanAdapter;
import sos.android.blesos.bleControler.Session;
import sos.android.blesos.util.Constant;
import sos.android.blesos.util.Utility;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DeviceScanActivity extends BaseActivity {

    private static final String TAG = DeviceScanActivity.class.getSimpleName();
    /**
     * default scan period is 10 seconds
     */
    private static final long SCAN_PERIOD = 10000;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    private ScanCallback mScanCallback;
    /**
     * Gatt call back
     */
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (Constant.DEBUG) {
                Log.d("onConnectionStateChange", "Status: " + status);
            }
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    if (Constant.DEBUG) {
                        Log.d("gattCallback", "STATE_CONNECTED");
                    }
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    if (Constant.DEBUG) {
                        Log.d("gattCallback", "STATE_DISCONNECTED");
                    }
                    break;
                default:
                    if (Constant.DEBUG) {
                        Log.d("gattCallback", "STATE_OTHER");
                    }
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            if (Constant.DEBUG) {
                Log.d("onServicesDiscovered", services.toString());
            }
            gatt.readCharacteristic(services.get(1).getCharacteristics().get
                    (0));
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            if (Constant.DEBUG) {
                Log.d("onCharacteristicRead", characteristic.toString());
            }
            gatt.disconnect();
        }
    };
    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private RecyclerView mRecyclerView;
    private ScanAdapter mScanAdapter;
    private boolean mScanning;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    /**
     * BLE scan callback below 21
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Constant.DEBUG) {
                                Log.i("onLeScan", device.toString());
                            }
                            mScanAdapter.addDevice(device);
                        }
                    });
                }
            };

    /**
     * Bluetooth state changed receiver
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if (Constant.DEBUG) {
                            Log.d(TAG, "Bluetooth off");
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if (Constant.DEBUG) {
                            Log.d(TAG, "Turning Bluetooth off...");
                        }
                        break;
                    case BluetoothAdapter.STATE_ON:
                        if (Constant.DEBUG) {
                            Log.d(TAG, "Bluetooth on");
                        }
                        scanBLE();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        if (Constant.DEBUG) {
                            Log.d(TAG, "Turning Bluetooth on...");
                        }
                        break;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_layout);

        //show actionbar home button
        isShowActionBarHomeButton(true);

        mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utility.showToast(getString(R.string.alert__lbl__bluetooth_not_supported));
            finish();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int rc = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (rc != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }

        //Get bluetooth mScanAdapter
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        initializeView();

        if (mBluetoothAdapter != null) {
            Set<BluetoothDevice> bluetoothDevices = mBluetoothAdapter.getBondedDevices();
            if (bluetoothDevices != null && bluetoothDevices.size() > 0) {
                for (BluetoothDevice bluetoothDevice :
                        bluetoothDevices) {
                    if (bluetoothDevice.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                        mScanAdapter.addDevice(bluetoothDevice);
                    }
                }
            }

            //Connected devices
            List<BluetoothDevice> devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
            for (BluetoothDevice device : devices) {
                if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                    Session.getInstance().setBLEConnectedDevices(device.getAddress());
                    mScanAdapter.addDevice(device);
                }
            }
        }

        /**
         * BLE scan callback above 21
         */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mScanCallback = new ScanCallback() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    if (Constant.DEBUG) {
                        Log.d("callbackType", String.valueOf(callbackType));
                        Log.d("result", result.toString());
                    }
                    final BluetoothDevice btDevice = result.getDevice();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mScanAdapter.addDevice(btDevice);
                        }
                    });
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

        if (mBluetoothAdapter.isEnabled()) {
            //scan BLE devices
            scanBLE();
        }

    }

    /**
     * initialize a layout view
     */
    private void initializeView() {
        //swipe refresh view
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handleRefreshSubmit();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        //set mScanAdapter
        mScanAdapter = new ScanAdapter(this);
        mRecyclerView.setAdapter(mScanAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_loader).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_loader).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                scanBLE();
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
            case R.id.menu_refresh:
                handleRefreshSubmit();
                break;
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

    /**
     * refresh the Ble scan
     */
    private void handleRefreshSubmit() {
        mScanAdapter.clear();
        scanLeDevice(false);
        scanBLE();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, SCAN_PERIOD);
    }

    /**
     * scan ble connections
     */
    private void scanBLE() {
        setProgressBarIndeterminateVisibility(true);
        if (Build.VERSION.SDK_INT >= 21) {
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<ScanFilter>();
        }
        scanLeDevice(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register for broadcasts on BluetoothAdapter state change
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (mScanAdapter != null) {
            mScanAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            if (mLEScanner != null) {
                scanLeDevice(false);
            }
        }
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_CANCELED) {
                //Bluetooth not enabled.
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * start scanning the BLE devices
     *
     * @param enable - ENABLE/DISABLE the BLE scan
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    setProgressBarIndeterminateVisibility(false);
                    invalidateOptionsMenu();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        if (mBluetoothAdapter != null)
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        if (mLEScanner != null)
                            mLEScanner.stopScan(mScanCallback);
                    }
                }
            }, SCAN_PERIOD);
            mScanning = true;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                if (mBluetoothAdapter != null)
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                if (mLEScanner != null)
                    mLEScanner.startScan(filters, settings, mScanCallback);
            }
        } else {
            mScanning = false;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                if (mBluetoothAdapter != null)
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                if (mLEScanner != null)
                    mLEScanner.stopScan(mScanCallback);
            }
        }
        invalidateOptionsMenu();
    }

    /**
     * connect to the other BLE device
     *
     * @param device
     */
    private void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
            scanLeDevice(false);// will stop after first device detection
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Constant.DEBUG) {
                        Log.d(TAG, "coarse location permission granted");
                    }
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getString(R.string.dialog__lbl__functionality_limited));
                    builder.setMessage(getString(R.string.dialog__lbl__location_access_not_granted));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

}