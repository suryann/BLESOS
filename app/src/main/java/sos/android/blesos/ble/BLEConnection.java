package sos.android.blesos.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import sos.android.blesos.application.BaseApplication;
import sos.android.blesos.bleControler.Session;
import sos.android.blesos.service.BLEService;
import sos.android.blesos.util.Constant;
import sos.android.blesos.util.Utility;

/**
 * Created by Radso Technologies on 3/31/2016.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLEConnection {
    //Broadcast action
    public final static String ACTION_GATT_CONNECTED =
            "sos.android.blesos.ble.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "sos.android.blesos.ble.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "sos.android.blesos.ble.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "sos.android.blesos.ble.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "sos.android.blesos.ble.EXTRA_DATA";
    public final static String EXTRA_BYTE_DATA =
            "sos.android.blesos.ble.EXTRA_BYTE_DATA";

    //filter UUID
    public final static UUID UUID_SERIAL_DATA_TRANSFER =
            UUID.fromString(GattAttributes.SERIAL_PORT_DATA_MEASUREMENT);

    private static final String TAG = BLEConnection.class.getSimpleName();
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    private static BLEConnection bleConnection = null;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    /**
     * show toast message
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String data = bundle.getString(Constant.BUNDLE_KEY_BLE_STATUS);
            if (!TextUtils.isEmpty(data)) {
                Utility.showToast(data);
            }
        }
    };

    //  callback methods for GATT events. For connection changes and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                handleConnect(gatt);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                handleDisconnect(gatt);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                if (Constant.DEBUG) {
                    Log.d(TAG, "onServicesDiscovered received: " + status);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (Constant.DEBUG) {
                Log.d(TAG, "onCharacteristicRead" + characteristic.toString());
            }
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            if (Constant.DEBUG) {
                Log.d(TAG, "onCharacteristicChanged" + characteristic.toString());
            }
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (Constant.DEBUG) {
                Log.d(TAG, "onDescriptorWrite is called");
            }
            //Once notifications are enabled, we move to the next sensor and start over with enable
            if (status == BluetoothGatt.GATT_SUCCESS) {

            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
                // this is where the tricky part comes

                if (gatt.getDevice().getBondState() == BluetoothDevice.BOND_NONE) {

                } else {

                }
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            if (Constant.DEBUG) {
                Log.d(TAG, "onDescriptorRead is called");
            }
        }
    };

    private BLEConnection() {
        initialize();
    }

    /**
     * get the bleConnection instance
     *
     * @return
     */
    public static BLEConnection getInstance() {
        if (bleConnection == null) {
            bleConnection = new BLEConnection();
        }
        return bleConnection;
    }

    /**
     * handle connect the Gatt server
     *
     * @param gatt
     */
    private void handleConnect(BluetoothGatt gatt) {
        String intentAction = ACTION_GATT_CONNECTED;
        mConnectionState = STATE_CONNECTED;

        String address = gatt.getDevice().getAddress();

        //send a connected broadcast
        Intent intent = new Intent(intentAction);
        intent.putExtra(Constant.BUNDLE_KEY_BLE_ADDRESS, address);
        Utility.sendBroadcast(intent);

        //store connected device
        Session.getInstance().setBLEConnectedDevices(address);

        if (Constant.DEBUG) {
            Log.d(TAG, "Connected to GATT server.");
        }

        String notifyMessage = "The device(" + address + ") is connected to the BLE device.";

        //Create a notification
        Utility.createNotification(notifyMessage);

        //show toast message
        Message message = new Message();
        Bundle resBundle = new Bundle();
        resBundle.putString(Constant.BUNDLE_KEY_BLE_STATUS, notifyMessage);
        message.setData(resBundle);
        handler.sendMessage(message);

        if (gatt.getDevice().getBondState() == BluetoothDevice.BOND_BONDED) {
            try {
                synchronized (this) {
                    if (Constant.DEBUG) {
                        Log.d(TAG, "Waiting 1600 ms for a possible Service Changed indication...");
                    }
                    wait(1600);
                }
            } catch (InterruptedException e) {
                // Do nothing
            }
        }

        // Attempts to discover services after successful connection.
        boolean discover = mBluetoothGatt.discoverServices();
        if (Constant.DEBUG) {
            Log.d(TAG, "Attempting to start service discovery:" + discover
            );
        }
    }

    /**
     * handle disconnection from GATT server
     *
     * @param gatt
     */
    private void handleDisconnect(BluetoothGatt gatt) {
        String intentAction = ACTION_GATT_DISCONNECTED;
        mConnectionState = STATE_DISCONNECTED;

        //remove connected device from session
        Session.getInstance().removeBLEConnectedDevices(gatt.getDevice().getAddress());
        if (Constant.DEBUG) {
            Log.d(TAG, "Disconnected from GATT server.");
        }

        String notifyMessage = "The device(" + gatt.getDevice().getAddress() + ") is disconnected from GATT server.";
        //Create a notification
        Utility.createNotification(notifyMessage);

        //show toast
        Message message = new Message();
        Bundle resBundle = new Bundle();
        resBundle.putString(Constant.BUNDLE_KEY_BLE_STATUS, notifyMessage);
        message.setData(resBundle);
        handler.sendMessage(message);

        //send a disconnected broadcast
        Intent intent = new Intent(intentAction);
        intent.putExtra(Constant.BUNDLE_KEY_BLE_ADDRESS, gatt.getDevice().getAddress());
        Utility.sendBroadcast(intent);

        //stop ble connection service
        BaseApplication.getApplication().stopService(new Intent(BaseApplication.getApplication(), BLEService.class));
    }

    /**
     * send a broadcast
     *
     * @param action
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        Utility.sendBroadcast(intent);
    }

    /**
     * send a broadcast
     *
     * @param action
     */
    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        if (UUID_SERIAL_DATA_TRANSFER.equals(characteristic.getUuid())) { //Serial port service
            final byte[] data = characteristic.getValue();

            if (Constant.DEBUG) {
                Log.d(TAG, "Received data:: " + new String(data));
            }

            //store received data from BLE
            //LogFile.getInstance().saveInFile("Received data:", new String(data));

            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
                intent.putExtra(EXTRA_BYTE_DATA, data);
            }
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        Utility.sendBroadcast(intent);
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    private boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) BaseApplication.getApplication().getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * get  bonded devices
     *
     * @return
     */
    public List<BluetoothDevice> getBondedBLEDevices() {
        if (mBluetoothManager != null) {
            List<BluetoothDevice> devices = new ArrayList<>();
            Set<BluetoothDevice> bluetoothDevices = mBluetoothAdapter.getBondedDevices();
            if (bluetoothDevices != null && bluetoothDevices.size() > 0) {
                for (BluetoothDevice device :
                        bluetoothDevices) {
                    if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                        devices.add(device);
                    }
                }
            }

            return devices;
        }
        return null;
    }

    /**
     * get connected  devices
     *
     * @return
     */
    public List<BluetoothDevice> getConnectedBLEDevices() {
        List<BluetoothDevice> devices = new ArrayList<>();
        //Connected devices
        List<BluetoothDevice> connectedDevices = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
        for (BluetoothDevice device : connectedDevices) {
            if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                devices.add(device);
            }
        }
        return devices;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     */
    public boolean connect(final String address) {
        return connect(address, false);
    }

    /**
     * Connects automatically to the GATT server hosted on the Bluetooth LE device.
     */
    public boolean autoConnect(final String address) {
        return connect(address, true);
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address, boolean isAutoConnect) {
        if (mBluetoothAdapter == null || address == null) {
            if (Constant.DEBUG) {
                Log.d(TAG, "BluetoothAdapter not initialized or unspecified address.");
            }
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            if (Constant.DEBUG) {
                Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            }
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            if (Constant.DEBUG) {
                Log.d(TAG, "Device not found.  Unable to connect.");
            }
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(BaseApplication.getApplication(), isAutoConnect, mGattCallback);
        if (Constant.DEBUG) {
            Log.d(TAG, "Trying to create a new connection.");
        }
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            if (Constant.DEBUG) {
                Log.e(TAG, "BluetoothAdapter not initialized");
            }
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            if (Constant.DEBUG) {
                Log.e(TAG, "BluetoothAdapter not initialized");
            }
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            if (Constant.DEBUG) {
                Log.e(TAG, "BluetoothAdapter not initialized");
            }
            return;
        }

        boolean isSuccess = BLEUtil.setCharacteristicNotification(mBluetoothGatt, characteristic, enabled);

        if (enabled) {
            Utility.showToast("Enable characteristics notifications.");
        } else {
            Utility.showToast("Disable characteristics notifications.");
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }

    /**
     * check whether the device is bonded or not
     *
     * @return
     */
    public boolean isBonded() {
        if(mBluetoothGatt!=null){
            BluetoothDevice bluetoothDevice = mBluetoothGatt.getDevice();
            return BLEUtil.isBonded(bluetoothDevice);
        } else {
            return false;
        }
    }

    /**
     * check whether the device is bonded or not
     *
     * @return
     */
    public boolean isBonded(String address) {
        if (mBluetoothAdapter == null) {
            return false;
        }
        BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
        return BLEUtil.isBonded(bluetoothDevice);
    }

    /**
     * check whether the device is connected or not
     *
     * @param address
     * @return
     */
    public boolean isConnected(String address) {
        if (mBluetoothAdapter == null) {
            return false;
        }
        boolean isConnected = false;
        List<BluetoothDevice> bluetoothDevices = getConnectedBLEDevices();
        if (bluetoothDevices != null && bluetoothDevices.size() > 0) {
            for (BluetoothDevice device : bluetoothDevices) {
                if (device.getAddress().equalsIgnoreCase(address)) {
                    isConnected = true;
                    break;
                }
            }
        }
        return isConnected;
    }

    /**
     * create a bond
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void createBond() {
        BluetoothDevice bluetoothDevice = mBluetoothGatt.getDevice();
        BLEUtil.createBond(bluetoothDevice);
    }


    /**
     * remove bond using reflection
     */
    public void unBondDevice(){
        BluetoothDevice device = mBluetoothGatt.getDevice();
        BLEUtil.removeBond(device);
    }


    /**
     * send data to other BLE device
     *
     * @param value
     */
    public boolean sendDataToBLE(byte[] value) {
        //check if the service is available on the device
        BluetoothGattService service = mBluetoothGatt.getService(UUID.fromString(GattAttributes.SERIAL_PORT_DATA_SERVICE));
        if (service == null) {
            if (Constant.DEBUG) {
                Log.d(TAG, "Custom BLE Service not found");
            }
            return false;
        }

        // get the read characteristic from the service
        BluetoothGattCharacteristic mWriteCharacteristic = service.getCharacteristic(UUID.fromString(GattAttributes.SERIAL_PORT_DATA_MEASUREMENT));
        mWriteCharacteristic.setValue(value);
        mWriteCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

        if (mBluetoothGatt.writeCharacteristic(mWriteCharacteristic)) {
            return true;
        } else {
            if (Constant.DEBUG) {
                Log.d(TAG, "Failed to write characteristic");
            }
            return false;
        }
    }
}
