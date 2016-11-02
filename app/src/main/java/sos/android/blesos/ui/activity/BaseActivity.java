package sos.android.blesos.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import sos.android.blesos.ble.BLEConnection;
import sos.android.blesos.ble.BLEUtil;
import sos.android.blesos.util.Constant;
import sos.android.blesos.util.Utility;


/**
 * Created by soorianarayanan on 13/10/16.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BaseActivity extends AppCompatActivity {
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * show home up button in action bar
     *
     * @param isShow
     */
    public void isShowActionBarHomeButton(boolean isShow) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(isShow);
        actionBar.setDisplayHomeAsUpEnabled(isShow);
    }

    /**
     * set action bar title
     *
     * @param title
     */
    public void setActionbarTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));
        } else {
            getSupportActionBar().setTitle("");
        }
    }


}
