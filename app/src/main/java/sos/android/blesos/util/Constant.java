package sos.android.blesos.util;

import sos.android.blesos.R;
import sos.android.blesos.application.BaseApplication;

/**
 * Created by soorianarayanan on 16/10/16.
 */

public interface Constant {
    boolean DEBUG = false;

    public static final String SMS_SENT = BaseApplication.appContext.getString(R.string.sms_sent);
    public static final String SMS_DELIVERED = BaseApplication.appContext.getString(R.string.sms_delivered);
    public static final int MAX_SMS_MESSAGE_LENGTH = 160;

    public static final String user = "USER";

    public static String GOOGLELINK	= "http://maps.google.com/maps/?q=";
    String LATITUDE = "Latitude";
    String LONGITUDE = "Longitude";
    String PHONENUMBER = "phoneNumber";

    //=====BUNDLE KEY========
    String BUNDLE_KEY_BLE_ADDRESS = "bundle_key_ble_address";
    String BUNDLE_KEY_BLE_STATUS = "bundle_key_ble_status";
}
