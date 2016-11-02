package sos.android.blesos.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import sos.android.blesos.R;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    ArrayList<HashMap<String, String>> mGattServiceData = new ArrayList<HashMap<String, String>>();
    ArrayList<ArrayList<HashMap<String, String>>> mGattCharacteristicData
            = new ArrayList<ArrayList<HashMap<String, String>>>();
    private Activity context;

    public ExpandableListAdapter(Activity context, ArrayList<HashMap<String, String>> gattServiceData,
                                 ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData) {
        this.context = context;
        this.mGattServiceData = gattServiceData;
        this.mGattCharacteristicData = gattCharacteristicData;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return mGattCharacteristicData.get(groupPosition).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        HashMap<String, String> childData = (HashMap<String, String>) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_ble_child_item, null);
        }

        TextView name = (TextView) convertView.findViewById(R.id.characteristic__tv__name);
        TextView uuid = (TextView) convertView.findViewById(R.id.characteristic__tv__uuid);

        if (childData != null) {
            name.setText(childData.get("NAME"));
            uuid.setText("UUID: " + childData.get("UUID"));
        }
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return mGattCharacteristicData.get(groupPosition).size();
    }

    public Object getGroup(int groupPosition) {
        HashMap<String, String> groupItem = mGattServiceData.get(groupPosition);
        groupItem.get("NAME");
        groupItem.get("UUID");
        return groupItem;
    }

    public int getGroupCount() {
        return mGattServiceData.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        HashMap<String, String> groupItem = (HashMap<String, String>) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_ble_group_item,
                    null);
        }
        TextView name = (TextView) convertView.findViewById(R.id.service__tv__group_name);
        TextView uuid = (TextView) convertView.findViewById(R.id.service__tv__group_uuid);

        if (groupItem != null) {
            name.setText(groupItem.get("NAME"));
            uuid.setText("UUID: " + groupItem.get("UUID"));
        }
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}