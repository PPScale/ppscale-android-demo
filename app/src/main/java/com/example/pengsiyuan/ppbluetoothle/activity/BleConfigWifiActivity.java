package com.example.pengsiyuan.ppbluetoothle.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.pengsiyuan.ppbluetoothle.DBManager;
import com.example.pengsiyuan.ppbluetoothle.R;
import com.example.pengsiyuan.ppbluetoothle.model.DeviceModel;
import com.example.pengsiyuan.ppbluetoothle.util.PPUtil;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.bmdj.PPBMDJConnectInterface;
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiInterface;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.PPHistoryDataInterface;
import com.peng.ppscale.business.ble.listener.PPLockDataInterface;
import com.peng.ppscale.business.ble.listener.PPProcessDateInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.device.PPDeviceType;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPBodyBaseModel;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPDeviceModel;

import java.util.ArrayList;
import java.util.List;

public class BleConfigWifiActivity extends AppCompatActivity {

    private PPScale ppScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_config_wifi);


        ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
        protocalFilter.setConfigWifiInterface(new PPConfigWifiInterface() {
            @Override
            public void monitorConfigState(String sn) {
                //拿到sn 处理业务逻辑
                Logger.e("xxxxxxxxxxxx-" + sn);
            }
        });

        ppScale = new PPScale.Builder(this)
                .setProtocalFilterImpl(protocalFilter)
                .setBleOptions(getBleOptions())
                .setBleStateInterface(bleStateInterface)
                .build();
        ppScale.startSearchBluetoothScaleWithMacAddressList();

    }

    private BleOptions getBleOptions() {
        return new BleOptions.Builder()
                .setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_CONFIG_WIFI)
                .setPassword("12345678")
                .setSsid("IT05-2.4G")
                .build();
    }

    PPBleStateInterface bleStateInterface = new PPBleStateInterface() {
        @Override
        public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState) {
            if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                Logger.d("设备已连接");
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                Logger.d("设备连接中");
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                Logger.d("设备已断开");
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateStop) {
                Logger.d("停止扫描");
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                Logger.d("扫描中");
            } else {
                Logger.e("蓝牙状态异常");
            }
        }

        @Override
        public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                Logger.e("系统蓝牙断开");
                Toast.makeText(BleConfigWifiActivity.this, "系统蓝牙断开", Toast.LENGTH_SHORT).show();
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                Logger.d("系统蓝牙打开");
                Toast.makeText(BleConfigWifiActivity.this, "系统蓝牙打开", Toast.LENGTH_SHORT).show();
            } else {
                Logger.e("系统蓝牙异常");
            }
        }
    };

    private ProtocalFilterImpl getProtocalFilter() {
        ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
        return protocalFilter;
    }
}
