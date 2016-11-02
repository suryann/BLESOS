package sos.android.blesos.ui.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sos.android.blesos.R;
import sos.android.blesos.adapter.ExpandableListAdapter;
import sos.android.blesos.ble.BLEConnection;
import sos.android.blesos.ble.GattAttributes;
import sos.android.blesos.service.BLEService;
import sos.android.blesos.util.Constant;
import sos.android.blesos.util.Utility;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DeviceControlActivity extends BaseActivity {
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private boolean isPaired;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private TextView mBondState;
    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BLEConnection mBLEConnection;

    /**
     * click listener for list view
     */
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBLEConnection.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBLEConnection.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBLEConnection.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
            };

    /**
     * BLE connection call back
     */
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BLEConnection.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.ble__lbl__connected);
                checkBondState();
            } else if (BLEConnection.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.ble__lbl__disconnected);
                clearUI();
                checkBondState();
            } else if (BLEConnection.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBLEConnection.getSupportedGattServices());
            } else if (BLEConnection.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BLEConnection.EXTRA_DATA));
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    isPaired = true;
                    updateBondState(getString(R.string.ble__lbl__bonded));
                    invalidateOptionsMenu();
                    showToast(getString(R.string.ble__lbl__paired));
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    isPaired = false;
                    updateBondState(getString(R.string.ble__lbl__not_bonded));
                    invalidateOptionsMenu();
                    showToast(getString(R.string.ble__lbl__unpaired));
                }

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Discover new device
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEConnection.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLEConnection.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BLEConnection.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BLEConnection.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        return intentFilter;
    }

    private void checkBondState() {
        //set bond state
        boolean isTrue = mBLEConnection.isBonded();
        if (isTrue) {
            updateBondState(getString(R.string.ble__lbl__bonded));
        } else {
            updateBondState(getString(R.string.ble__lbl__not_bonded));
        }
        invalidateOptionsMenu();
    }

    private void showToast(String message) {
        Utility.showToast(message);
    }

    private void clearUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGattServicesList.setAdapter((BaseExpandableListAdapter) null);
                mDataField.setText(R.string.ble__lbl__no_data);
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_control_layout);

        //show action bar home button
        isShowActionBarHomeButton(true);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        //setAction bar title
        setActionbarTitle(mDeviceName);

        // Sets up UI references.
        ((TextView) findViewById(R.id.scan__tv__device_address)).setText(mDeviceAddress);

        //Service expandable list
        mGattServicesList = (ExpandableListView) findViewById(R.id.scan__list__gatt_services);
        mGattServicesList.setGroupIndicator(null);
        mGattServicesList.setChildIndicator(null);
        mGattServicesList.setChildDivider(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
//        mGattServicesList.setDivider(new ColorDrawable(getResources().getColor(R.color.LightGrey)));
        mGattServicesList.setOnChildClickListener(servicesListClickListner);

        //Connection state
        mConnectionState = (TextView) findViewById(R.id.scan__tv__connection_state);


        //Bond state
        mBondState = (TextView) findViewById(R.id.scan__tv__bond_state);


        //data view
        mDataField = (TextView) findViewById(R.id.scan__tv__data_value);

        //create an instance
        mBLEConnection = BLEConnection.getInstance();

        //checking Connected and bonded device status
        if (mBLEConnection.isBonded(mDeviceAddress)) {
            mBondState.setText(getString(R.string.ble__lbl__bonded));
        } else {
            mBondState.setText(getString(R.string.ble__lbl__not_bonded));
        }

        if (mBLEConnection.isConnected(mDeviceAddress)) {
            mConnected = true;
            mConnectionState.setText(R.string.ble__lbl__connected);
            displayGattServices(mBLEConnection.getSupportedGattServices());
        } else {
            mConnectionState.setText(R.string.ble__lbl__disconnected);
        }

        invalidateOptionsMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//        if (mBLEConnection != null) {
//            final boolean result = mBLEConnection.connect(mDeviceAddress);
//            Log.d(TAG, "Connect request result=" + result);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ble__services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
            menu.findItem(R.id.menu_enable_services).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
            menu.findItem(R.id.menu_enable_services).setVisible(false);
        }

        if (isPaired) {
            menu.findItem(R.id.menu_bond).setTitle(R.string.menu__lbl__remove_bond);
        } else {
            menu.findItem(R.id.menu_bond).setTitle(R.string.menu__lbl__create_bond);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                handelConnectBLE();
                return true;
            case R.id.menu_disconnect:
                mBLEConnection.disconnect();
                return true;
            case R.id.menu_bond:
                if (mConnected) {
                    if (!mBLEConnection.isBonded()) {
                        //pairing
                        mBLEConnection.createBond();
                    } else {
                        //for unpairing
                        mBLEConnection.unBondDevice();
                        mBLEConnection.disconnect();
                    }
                }
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_enable_services:
                handleEnableAllServices();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Enable all service and read all characteristics
     */
    private void handleEnableAllServices() {
        if (!mConnected) {
            return;
        }
        List<BluetoothGattService> gattServices = mBLEConnection.getSupportedGattServices();
        if (gattServices != null && gattServices.size() > 0) {
            for (BluetoothGattService gattService : gattServices) {
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();

                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    String uuid = gattCharacteristic.getUuid().toString();
                    final int charaProp = gattCharacteristic.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        mBLEConnection.readCharacteristic(gattCharacteristic);
                    }

                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mBLEConnection.setCharacteristicNotification(
                                gattCharacteristic, true);
                    }
                }
            }
        }
    }

    /**
     * start a service for connect ble devices
     */
    private void handelConnectBLE() {
        //start a service to connect BLE
        Intent gattServiceIntent = new Intent(this, BLEService.class);
        gattServiceIntent.putExtra(Constant.BUNDLE_KEY_BLE_ADDRESS, mDeviceAddress);
        //stop service
        stopService(gattServiceIntent);
        //start service
        startService(gattServiceIntent);
    }

    /**
     * update connection state
     *
     * @param resourceId
     */
    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    /**
     * update bond state
     *
     * @param state
     */
    private void updateBondState(final String state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBondState.setText(state);
            }
        });
    }

    /**
     * display data
     *
     * @param data
     */
    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

    /**
     * iterate through the supported GATT Services/Characteristics.
     *
     * @param gattServices
     */
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.ble__lbl__unknown_service);
        String unknownCharaString = getResources().getString(R.string.ble__lbl__unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, GattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, GattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        //List view
        ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(this,
                gattServiceData, gattCharacteristicData);
        mGattServicesList.setAdapter(expandableListAdapter);
    }
}
