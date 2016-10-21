package sos.android.blesos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import sos.android.blesos.R;
import sos.android.blesos.util.Utils;

public class MessageReceiver extends BroadcastReceiver {

    private String [] messages;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        this.context = context;
        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                String googleLink[];
                String address[];
                String Latitude = null;
                String Longitude = null;

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    if (message.contains(context.getResources().getString(R.string.key))) {
                        messages = message.split(";");
                        for (String msg : messages) {
                            if(msg.contains(" GoogleLink ")){
                                googleLink = msg.split(":");
                                Utils.openMap(googleLink[1]);
                                Utils.createNotification(context.getString(R.string.app_name), message, googleLink[1]);
                            }
                            if(msg.contains("Latitude")){
                                Latitude = msg.split(":")[1];
                            }
                            if(msg.contains("Longitude")){
                                Longitude = msg.split(":")[1];
                            }
                        }
                        Utils.showRoute(Latitude, Longitude);
                    }

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
