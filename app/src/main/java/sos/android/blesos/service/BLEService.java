package sos.android.blesos.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import sos.android.blesos.ble.BLEConnection;
import sos.android.blesos.bleControler.Session;
import sos.android.blesos.util.Constant;


/**
 * Created by Radso Technologies on 3/31/2016.
 */
public class BLEService extends Service {

    private BLEConnection bleConnection;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }
        String address = intent.getStringExtra(Constant.BUNDLE_KEY_BLE_ADDRESS);
        if (!TextUtils.isEmpty(address)) {
            bleConnection = BLEConnection.getInstance();
            bleConnection.connect(address, true);
        }
        return START_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bleConnection != null) {
            Session.getInstance().clear();
            try {
                bleConnection.unBondDevice();
            } catch (Exception e) {
                e.printStackTrace();
            }
            bleConnection.close();
            bleConnection = null;
        }
    }
}
