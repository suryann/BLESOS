package sos.android.blesos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import sos.android.blesos.service.AlarmService;

public class AutoStart extends BroadcastReceiver {
    public AutoStart() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            context.startService(new Intent(context, AlarmService.class));
        }
    }
}
