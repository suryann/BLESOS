package sos.android.blesos.bleControler;

import java.util.HashSet;
import java.util.Set;

/**
 * This singleton class is used to store/retrieve the data in/from the session.
 * <p>
 * Created by Radso Technologies on 2/8/2016.
 */
public class Session {
    private static Session session = null;
    private static Set<String> mConnectedDevices = new HashSet<>();

    private Session() {
    }

    /**
     * get the session instance
     *
     * @return
     */
    public static Session getInstance() {
        if (session == null) {
            session = new Session();
        }
        return session;
    }

    /**
     * clear local session data
     */
    public void clear() {
        session = null;
    }

    /**
     * clear all BLE connected devices
     */
    public void clearAllBLEConnectedDevices() {
        if (mConnectedDevices != null) {
            mConnectedDevices.clear();
        }
    }

    /**
     * get all BLE connected devices
     *
     * @return
     */
    public Set<String> getBLEConnectedDevices() {
        return mConnectedDevices;
    }

    /**
     * BLE connected devices
     */
    public void setBLEConnectedDevices(String address) {
        mConnectedDevices.add(address);
    }

    /**
     * remove BLE connected device
     *
     * @param address
     * @return
     */
    public boolean removeBLEConnectedDevices(String address) {
        if (mConnectedDevices.contains(address)) {
            mConnectedDevices.remove(address);
            return true;
        }
        return false;
    }


}
