package sos.android.blesos.sendmsg;

import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;

import java.util.ArrayList;

import sos.android.blesos.application.BaseApplication;
import sos.android.blesos.util.Constant;

/**
 * Created by soorianarayanan on 16/10/16.
 */

public class SendMessage {

    String message;

    public SendMessage(ArrayList<String> receipientList, String message) {
        this.message = message;
        if (!receipientList.isEmpty()) {
            for (String msgReceiver : receipientList) {
                sendSms(msgReceiver);
            }
        }
    }

    public void sendSms(String phonenumber) {
        SmsManager manager = SmsManager.getDefault();

        PendingIntent piSend = PendingIntent.getBroadcast(BaseApplication.appContext, 0, new Intent(Constant.SMS_SENT), 0);
        PendingIntent piDelivered = PendingIntent.getBroadcast(BaseApplication.appContext, 0, new Intent(Constant.SMS_DELIVERED), 0);

        int length = message.length();

        if (length > Constant.MAX_SMS_MESSAGE_LENGTH) {
            ArrayList<String> messagelist = manager.divideMessage(message);

            manager.sendMultipartTextMessage(phonenumber, null, messagelist, null, null);
        } else {
            manager.sendTextMessage(phonenumber, null, message, piSend, piDelivered);
        }
    }
}
