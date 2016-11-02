package sos.android.blesos.bleControler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * This is used for launching an activity..
 */
public enum ActivityController {

    INSTANCE, ActivityController;

    /**
     * launch activity..
     *
     * @param context
     * @param bundle
     * @param targetComponent
     */
    public void launchActivity(Context context, Bundle bundle, Class<?> targetComponent) {
        Intent intent = new Intent(context, targetComponent);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    /**
     * launch activity..
     *
     * @param context
     * @param targetComponent
     */
    public void launchActivity(Context context, Class<?> targetComponent) {
        launchActivity(context, null, targetComponent);
    }

}
