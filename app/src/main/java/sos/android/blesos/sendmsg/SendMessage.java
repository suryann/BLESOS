package sos.android.blesos.sendmsg;

import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;

import sos.android.blesos.application.BaseApplication;
import sos.android.blesos.util.Constant;

/**
 * Created by Radso Technologies on 16/10/16.
 */

public class SendMessage {

    private static final String TAG = SendMessage.class.getName();
    String message;

    public SendMessage(ArrayList<String> receipientList, String message) {
        this.message = message;
        if (!receipientList.isEmpty()) {
            for (final String msgReceiver : receipientList) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendSMS(msgReceiver);
                        Log.v(TAG, " SMS sent to "+msgReceiver);
                    }
                }).start();
            }
        }
    }

    private void sendSMS(String phoneNumber) {
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();
        PendingIntent sentPI = PendingIntent.getBroadcast(BaseApplication.appContext, 0,
                new Intent(BaseApplication.appContext, SmsSentReceiver.class), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(BaseApplication.appContext, 0,
                new Intent(BaseApplication.appContext, SmsDeliveredReceiver.class), 0);
        try {
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> mSMSMessage = sms.divideMessage(message);
            for (int i = 0; i < mSMSMessage.size(); i++) {
                sentPendingIntents.add(i, sentPI);
                deliveredPendingIntents.add(i, deliveredPI);
                sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
            }

            sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage,
                    sentPendingIntents, deliveredPendingIntents);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
