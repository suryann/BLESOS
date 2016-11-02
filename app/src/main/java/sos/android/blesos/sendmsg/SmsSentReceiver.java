package sos.android.blesos.sendmsg;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import sos.android.blesos.util.Utility;

/**
 * Created by soorianarayanan on 18/10/16.
 */
public class SmsSentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent arg1) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Utility.showToast("SMS Sent");
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Utility.showToast("SMS generic failure");
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Utility.showToast("SMS no service");
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Utility.showToast("SMS null PDU");
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Utility.showToast("SMS radio off");
                break;
        }
    }
}