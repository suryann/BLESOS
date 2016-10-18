package sos.android.blesos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;

import sos.android.blesos.R;
import sos.android.blesos.util.Utils;

public class MessageReceiver extends BroadcastReceiver {

    private ArrayList<String> messages;
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

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    if (message.contains(context.getResources().getString(R.string.key))) {
                        messages = new ArrayList<String>();
                        Utils.createNotification(context.getString(R.string.app_name), message);
                        messages.toArray(message.split(";"));
                        for (String msg : messages) {
                            if(msg.contains(" GoogleLink ")){
                                googleLink = msg.split(":");
                                openMap(googleLink[1]);
                            }
                        }

                    }

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openMap(String Googlelink) {
        Uri uri = Uri.parse("smsto:");
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", Googlelink);
        context.startActivity(it);
    }
}
