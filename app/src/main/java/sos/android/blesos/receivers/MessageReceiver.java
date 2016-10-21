package sos.android.blesos.receivers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import sos.android.blesos.R;
import sos.android.blesos.util.Utils;

public class MessageReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = MessageReceiver.class.getName();
    private String [] messages;
    private Context context;

    String Latitude = null;
    String Longitude = null;

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

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    if (message.contains(context.getResources().getString(R.string.key))) {
                        messages = message.split(";");
                        for (String msg : messages) {
                            if(msg.contains(" GoogleLink ")){
                                googleLink = msg.split(":");
                                String sendGoogleLink = googleLink[1]+googleLink[2];
                                //TODO later based on the requirement
//                                Utils.openMap(sendGoogleLink);
                                Log.v(TAG, "googleLink  "+sendGoogleLink);
                                Toast.makeText(context, "googleLink : "+sendGoogleLink,Toast.LENGTH_LONG).show();
                                Utils.createNotification(context.getString(R.string.app_name), message, sendGoogleLink);
                            }
                            if(msg.contains("Latitude")){
                                Latitude = msg.split(":")[1];
                                Log.v(TAG, "Latitude  "+Latitude);
                            }
                            if(msg.contains("Longitude")){
                                Longitude = msg.split(":")[1];
                                Log.v(TAG, "Longitude  "+Longitude);
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
