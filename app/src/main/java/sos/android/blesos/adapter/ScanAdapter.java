/**
 *
 */

package sos.android.blesos.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sos.android.blesos.R;
import sos.android.blesos.application.BaseApplication;
import sos.android.blesos.bleControler.ActivityController;
import sos.android.blesos.bleControler.Session;
import sos.android.blesos.receivers.ScanReceiver;
import sos.android.blesos.ui.activity.DeviceControlActivity;
import sos.android.blesos.util.SharedPreferenceUtil;
import sos.android.blesos.util.Utility;

/**
 * scan devices
 */
public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ViewHolder> {
    private List<BluetoothDevice> mDataModels = new ArrayList<BluetoothDevice>();
    private Activity mContext;

    public ScanAdapter(Activity context) {
        this.mContext = context;
    }

    /**
     * set the data model
     *
     * @param dataModels
     */
    public void setDataModels(List<BluetoothDevice> dataModels) {
        this.mDataModels = dataModels;
        notifyDataSetChanged();
    }

    /**
     * add device into adapter
     *
     * @param device
     */
    public void addDevice(BluetoothDevice device) {
        boolean isExist = false;
        if (mDataModels != null && mDataModels.size() == 0) {
            mDataModels.add(device);
            notifyDataSetChanged();
            return;
        }
        for (BluetoothDevice bluetoothDevice :
                mDataModels) {
            if (bluetoothDevice.getAddress().equalsIgnoreCase(device.getAddress())) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            mDataModels.add(device);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mDataModels.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_scan_devices, parent,
                false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        final BluetoothDevice dataModel = mDataModels.get(position);
        if (dataModel != null) {
            //device name
            if (TextUtils.isEmpty(dataModel.getName())) {
                holder.mNameTextView.setText("N/A");
            } else {
                holder.mNameTextView.setText(dataModel.getName());
            }

            //Filtered service
            String address = "5B:19:5B";
            if (dataModel.getAddress().contains(address)) {
                holder.mNameTextView.setText("KemSys");
            }

            //device address
            holder.mAddressTextView.setText(dataModel.getAddress());

            int bondState = dataModel.getBondState();
            if (bondState == BluetoothDevice.BOND_BONDED || bondState == BluetoothDevice.BOND_BONDING) {
                holder.mBondStateTextView.setText(mContext.getString(R.string.ble__lbl__bonded));
                holder.mBondStateTextView.setTextColor(mContext.getResources().getColor(R.color.Green));
            } else {
                holder.mBondStateTextView.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));
                holder.mBondStateTextView.setText(mContext.getString(R.string.ble__lbl__not_bonded));
            }

            holder.mConnectionStateTextView.setText(mContext.getString(R.string.ble__lbl__not_connected));
            holder.mConnectionStateTextView.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));

            //Connection state
            Set<String> connectedDevices = Session.getInstance().getBLEConnectedDevices();
            if (connectedDevices != null) {
                if (connectedDevices.contains(dataModel.getAddress())) {
                    holder.mConnectionStateTextView.setText(mContext.getString(R.string.ble__lbl__connected));
                    holder.mConnectionStateTextView.setTextColor(mContext.getResources().getColor(R.color.Green));
                    holder.mConnectButton.setVisibility(View.GONE);
                }
            }

        }

        //connect button click event
        holder.mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleConnectButtonSubmit(pos);
            }
        });

        holder.mStoreButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                SharedPreferenceUtil.getInstance().setStringValue(SharedPreferenceUtil.MAC_ADD, dataModel.getAddress());
                ScanReceiver.SetAlarm(BaseApplication.appContext);
                Utility.showToast("Address Stored");
            }
        });

        //row click event
        holder.mRowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleConnectButtonSubmit(pos);
            }
        });

    }

    private void handleConnectButtonSubmit(int pos) {
        BluetoothDevice dataModel = mDataModels.get(pos);
        if (dataModel != null) {
            Bundle bundle = new Bundle();
            bundle.putString(DeviceControlActivity.EXTRAS_DEVICE_NAME, dataModel.getName());
            bundle.putString(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, dataModel.getAddress());
            ActivityController.INSTANCE.launchActivity(mContext, bundle, DeviceControlActivity.class);
        }
    }

    /**
     * clear all data from adapter
     */
    public void clear() {
        mDataModels.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button mConnectButton;
        public Button mStoreButton;
        public TextView mNameTextView;
        private TextView mAddressTextView;
        private View mRowView;
        private TextView mBondStateTextView;
        private TextView mConnectionStateTextView;

        @SuppressLint("NewApi")
        public ViewHolder(View view) {
            super(view);
            mRowView = view;
            mNameTextView = (TextView) view.findViewById(R.id.scan__tv__name);
            mAddressTextView = (TextView) view.findViewById(R.id.scan__tv__address);
            mConnectionStateTextView = (TextView) view.findViewById(R.id.scan__tv__connection_state);
            mBondStateTextView = (TextView) view.findViewById(R.id.scan__tv__bond_state);
            mConnectButton = (Button) view.findViewById(R.id.scan__btn__connect);
            mStoreButton = (Button) view.findViewById(R.id.scan__btn__store);
        }

    }

}
