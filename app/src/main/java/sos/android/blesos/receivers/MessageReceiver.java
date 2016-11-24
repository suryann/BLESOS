package sos.android.blesos.receivers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.SmsMessage;
import android.util.Log;

import sos.android.blesos.R;
import sos.android.blesos.ui.activity.CallBackActivity;
import sos.android.blesos.util.Constant;
import sos.android.blesos.util.Utility;
import sos.android.blesos.util.Utils;

public class MessageReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = MessageReceiver.class.getName();
    private String[] messages;

    String Latitude = null;
    String Longitude = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {
            String key;

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                String googleLink[];
                String phoneNumber;

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    key = context.getResources().getString(R.string.key);
                    if (message.contains(key)) {
                        Log.v(TAG, "SMS received");
                        messages = message.split(";");
                        for (String msg : messages) {
                            if (msg.contains(" GoogleLink ")) {
                                googleLink = msg.split(":");
                                String sendGoogleLink = googleLink[1] + googleLink[2];
                                //TODO later based on the requirement
                                Log.v(TAG, "googleLink  " + sendGoogleLink);
                                Utility.showToast("googleLink : " + sendGoogleLink);
                                Utils.createNotification(context.getString(R.string.app_name), message, sendGoogleLink);
                            }
                            if (msg.contains(Constant.LATITUDE)) {
                                Latitude = msg.split(":")[1];
                                Log.v(TAG, Constant.LATITUDE + Latitude);
                            }
                            if (msg.contains(Constant.LONGITUDE)) {
                                Longitude = msg.split(":")[1];
                                Log.v(TAG, Constant.LONGITUDE + Longitude);
                            }
                        }
                        context.startActivity(new Intent(context, CallBackActivity.class).
                                putExtra(Constant.LATITUDE, Latitude).
                                putExtra(Constant.LONGITUDE, Longitude).
                                putExtra(Constant.PHONENUMBER, phoneNumber).
                                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        Utils.playSirenSound();
                        Log.v(TAG, "Starting Alarm");
                    }

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
