package sos.android.blesos.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import sos.android.blesos.application.BaseApplication;

/**
 * Created by Radso Technologies on 3/24/2016.
 */
public class Utility {

    /**
     * show toast message
     *
     * @param message
     */
    public static void showToast(String message) {
        Toast.makeText(BaseApplication.appContext, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * send broadcast to view
     *
     * @param intent
     */
    public static void sendBroadcast(Intent intent) {
        BaseApplication.appContext.sendBroadcast(intent);
    }

    /**
     * create a notification
     */
    public static void createNotification(String message) {
        NotificationManagerUtil.getInstance().createNotifications(BaseApplication.appContext, null, message);
    }

}
