package com.lefu.ppscale.wifi.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lefu.ppscale.wifi.R;
import com.lefu.ppscale.wifi.model.DeviceModel;
import com.peng.ppscale.business.device.PPDeviceType;

import java.util.List;

public class DeviceListAdapter extends ArrayAdapter {

    private final int resourceId;

    public DeviceListAdapter(@NonNull Context context, int resource, List<DeviceModel> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceModel deviceModel = (DeviceModel) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView nameText = (TextView) view.findViewById(R.id.device_name);
        TextView macText = (TextView) view.findViewById(R.id.device_mac);
        TextView tv_ssid = (TextView) view.findViewById(R.id.device_ssid);
        nameText.setText(deviceModel.getDeviceName());
        macText.setText(deviceModel.getDeviceMac());
        if (PPDeviceType.Scale.isConfigWifiScale(deviceModel.getDeviceName())) {
            if (!TextUtils.isEmpty(deviceModel.getSsid())) {
                tv_ssid.setText(deviceModel.getSsid());
            } else {
                tv_ssid.setText("去配网");
            }
        } else {
            tv_ssid.setVisibility(View.GONE);
        }
        return view;
    }
}
