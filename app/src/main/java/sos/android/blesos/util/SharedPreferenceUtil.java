
package sos.android.blesos.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Created by soorianarayanan on 16/10/16.
 */

public class SharedPreferenceUtil {

    private static final String TAG = SharedPreferenceUtil.class.getSimpleName();

    public static String USER_NAME = "user_name";
    public static String USER_PASSWORD = "user_password";

    public static String MAC_ADD = "mac_add";

    private static SharedPreferenceUtil instance;

    private SharedPreferences mSharedPrefs;

    private SharedPreferenceUtil(Context context) {
        mSharedPrefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    public void clear() {
        mSharedPrefs.edit().clear().commit();
    }


    public static void init(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceUtil(context);
        }
    }

    public static SharedPreferenceUtil getInstance() {
        if (instance == null) {
            throw new RuntimeException(
                    "Must run init(Application application) before an instance can be obtained");
        }
        return instance;
    }

    public String getStringValue(String key, String defaultvalue) {
        String value = mSharedPrefs.getString(key, defaultvalue);

        Log.i(TAG, "getStringValueCalled >>>>>>>>>>key:" + key + " value:" + value);
        return value;
    }

    public void setStringValue(String key, String value) {
        Editor mPrefsEditor = mSharedPrefs.edit();
        mPrefsEditor.putString(key, value);
        mPrefsEditor.commit();
        Log.i(TAG, "setStringValue>>>>>>>>>>key:" + key + " value:" + value);
    }

    public boolean getBooleanValue(String key, Boolean defaultvalue) {
        boolean status = mSharedPrefs.getBoolean(key, defaultvalue);
        Log.i(TAG, "getBooleanValueCalled >>>>>>>>>>key:" + key + " status:" + status);
        return status;
    }

    public void setBooleanValue(String key, boolean value) {
        Editor mPrefsEditor = mSharedPrefs.edit();
        mPrefsEditor.putBoolean(key, value);
        mPrefsEditor.commit();
        Log.i(TAG, "setBooleanValueCalled >>>>>>>>>>key:" + key + " status:" + value);
    }

    public int getIntValue(String key, int defaultvalue) {
        int value = mSharedPrefs.getInt(key, defaultvalue);
        Log.i(TAG, "getIntValue >>>>>>>>>>key:" + key + " status:" + value);
        return value;
    }

    public void setIntValue(String key, int value) {
        Editor mPrefsEditor = mSharedPrefs.edit();
        mPrefsEditor.putInt(key, value);
        mPrefsEditor.commit();
        Log.i(TAG, "setIntValue >>>>>>>>>>key:" + key + " status:" + value);
    }
}
