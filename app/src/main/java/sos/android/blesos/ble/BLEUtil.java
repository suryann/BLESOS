package sos.android.blesos.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import sos.android.blesos.application.BaseApplication;
import sos.android.blesos.bleControler.Session;

/**
 * Created by Radso Technologies on 3/31/2016.
 */
public class BLEUtil {
    private static final String TAG = BLEUtil.class.getSimpleName();


    /**
     * // For API level 18 and above, get a reference to BluetoothAdapter through
     * // BluetoothManager.
     * get bluetooth manager
     *
     * @return
     */
    public static BluetoothManager getBLEManager() {
        BluetoothManager bluetoothManager = (BluetoothManager) BaseApplication.getApplication().getSystemService(Context.BLUETOOTH_SERVICE);
        return bluetoothManager;
    }

    /**
     * get ble adapter
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static BluetoothAdapter getBLEAdapter() {
        BluetoothManager bleManager = getBLEManager();
        if (bleManager == null) {
            return null;
        }
        BluetoothAdapter adapter = bleManager.getAdapter();
        return adapter;
    }

    /**
     * get all bonded devices
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static Set<BluetoothDevice> getBondedBLEDevices() {
        BluetoothAdapter mBluetoothAdapter = getBLEAdapter();
        Set<BluetoothDevice> devices = new HashSet<>();
        if (mBluetoothAdapter != null) {
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
     * get connected  ble devices
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static List<BluetoothDevice> getConnectedBLEDevices() {
        List<BluetoothDevice> devices = new ArrayList<>();
        //Connected devices
        List<BluetoothDevice> connectedDevices = getBLEManager().getConnectedDevices(BluetoothProfile.GATT);
        for (BluetoothDevice device : connectedDevices) {
            if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                //store connected device
                Session.getInstance().setBLEConnectedDevices(device.getAddress());
                devices.add(device);
            }
        }
        return devices;
    }

    /**
     * check whether the device is bonded or not
     *
     * @return
     */
    public static boolean isBonded(BluetoothDevice bluetoothDevice) {
        int bondState = bluetoothDevice.getBondState();
        if (bondState == BluetoothDevice.BOND_BONDED) {
            return true;
        }
        return false;
    }

    /**
     * create a bond
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void createBond(BluetoothDevice bluetoothDevice) {
        boolean bondState;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bondState = bluetoothDevice.createBond();
        } else {
            bondState = createBondApi18(bluetoothDevice);
        }
        if (bondState) {
            Log.d(TAG, "Bond process is beginning");
        } else {
            Log.d(TAG, "Error in bond process.");
        }
    }

    /**
     * create bond for api leve 18
     *
     * @param device
     * @return
     */
    private static boolean createBondApi18(final BluetoothDevice device) {
        /*
         * There is a createBond() method in BluetoothDevice class but for now it's hidden. We will call it using reflections. It has been revealed in KitKat (Api19)
		 */
        try {
            final Method createBond = device.getClass().getMethod("createBond");
            if (createBond != null) {
                return (Boolean) createBond.invoke(device);
            }
        } catch (final Exception e) {
            Log.e(TAG, "An exception occurred while creating bond", e);
        }
        return false;
    }


    /**
     * Removes the bond information for the given device.
     *
     * @param device the device to unbound
     * @return <code>true</code> if operation succeeded, <code>false</code> otherwise
     */
    public static boolean removeBond(final BluetoothDevice device) {
        if (device.getBondState() == BluetoothDevice.BOND_NONE)
            return true;

        Log.d(TAG, "Removing bond information...");
        boolean result = false;
        /*
         * There is a removeBond() method in BluetoothDevice class but for now it's hidden. We will call it using reflections.
		 */
        try {
            final Method removeBond = device.getClass().getMethod("removeBond");
            if (removeBond != null) {
                result = (Boolean) removeBond.invoke(device);
            }
        } catch (final Exception e) {
            Log.e(TAG, "An exception occurred while removing bond information", e);
        }
        return result;
    }

    /**
     * Send custom data to BLE
     *
     * @param value
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void sendDataToBLE(BluetoothGatt mBluetoothGatt, String value, UUID serviceUUid, UUID charUUid) {

        if (mBluetoothGatt == null) {
            Log.d(TAG, "BluetoothAdapter not initialized");
            return;
        }
        /*check if the service is available on the device*/
        BluetoothGattService service = mBluetoothGatt.getService(serviceUUid);
        if (service == null) {
            Log.d(TAG, "Custom BLE Service not found");
            return;
        }

        /*get the read characteristic from the service*/
        BluetoothGattCharacteristic mWriteCharacteristic = service.getCharacteristic(charUUid);
        mWriteCharacteristic.setValue(value);
        mWriteCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

        if (mBluetoothGatt.writeCharacteristic(mWriteCharacteristic) == false) {
            Log.d(TAG, "Failed to write characteristic");
        }
    }

    /**
     * Enables or disables notification on a given characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean setCharacteristicNotification(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic characteristic,
                                                        boolean enabled) {
        if (bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }

        if (bluetoothGatt.setCharacteristicNotification(characteristic, enabled)) {
            if (enabled) {
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                        UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                if (descriptor != null) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    return bluetoothGatt.writeDescriptor(descriptor);
                }
                return false;
            } else {
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                        UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                if (descriptor != null) {
                    descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                    return bluetoothGatt.writeDescriptor(descriptor);
                }
                return false;
            }
        }

        return false;
    }
}
