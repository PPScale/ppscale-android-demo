package com.lefu.ppscale.ble.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.ble.model.DeviceModel;

import java.util.List;

public class DeviceListAdapter extends ArrayAdapter {

    private final int resourceId;

    public DeviceListAdapter(@NonNull Context context, int resource, List<DeviceModel> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        DeviceModel deviceModel = (DeviceModel) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView nameText = (TextView)view.findViewById(R.id.device_name);
        TextView macText = (TextView)view.findViewById(R.id.device_mac);
        nameText.setText(deviceModel.getDeviceName());
        macText.setText(deviceModel.getDeviceMac());
        return view;
    }
}
