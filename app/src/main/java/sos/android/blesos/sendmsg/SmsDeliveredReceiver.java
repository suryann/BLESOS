package sos.android.blesos.sendmsg;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import sos.android.blesos.util.Utility;

/**
 * Created by Radso Technologies on 18/10/16.
 */
public class SmsDeliveredReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent arg1) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Utility.showToast("SMS delivered");
                break;
            case Activity.RESULT_CANCELED:
                Utility.showToast("SMS not delivered");
                break;
        }
    }
}
