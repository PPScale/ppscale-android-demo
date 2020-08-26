package com.example.pengsiyuan.ppbluetoothle.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pengsiyuan.ppbluetoothle.DBManager;
import com.example.pengsiyuan.ppbluetoothle.R;
import com.example.pengsiyuan.ppbluetoothle.model.DeviceModel;
import com.example.pengsiyuan.ppbluetoothle.util.DataUtil;
import com.example.pengsiyuan.ppbluetoothle.util.PPUtil;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.PPHistoryDataInterface;
import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.business.ble.listener.PPLockDataInterface;
import com.peng.ppscale.business.ble.listener.PPProcessDateInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPBodyBaseModel;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPUserModel;

import java.util.ArrayList;
import java.util.List;

public class BindingDeviceActivity extends Activity {

    TextView weightTextView;
    PPScale ppScale;

    public static final String UNIT_TYPE = "unitType";
    //0是绑定设备 1是搜索已有设备
    public static final String SEARCH_TYPE = "SearchType";

    private PPUnitType unitType;
    private PPUserModel userModel;
    private int searchType;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingdevice);
        weightTextView = findViewById(R.id.weightTextView);

        int unit = getIntent().getIntExtra(UNIT_TYPE, 0);

        unitType = PPUnitType.values()[unit];

        searchType = getIntent().getIntExtra(SEARCH_TYPE, 0);

        userModel = DataUtil.util().getUserModel();

        bindingDevice();
    }

    private BleOptions getBleOptions() {
        return new BleOptions.Builder()
                .setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_ALL)
                .setUnitType(unitType)
                .build();
    }

    private ProtocalFilterImpl getProtocalFilter() {
        ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
        protocalFilter.setPPProcessDateInterface(new PPProcessDateInterface() {
            @Override
            public void monitorProcessData(PPBodyBaseModel bodyBaseModel) {
                Logger.d("bodyBaseModel scaleName " + bodyBaseModel.getScaleName());
                String weightStr = PPUtil.getWeight(unitType, bodyBaseModel.getPpWeightKg());
                weightTextView.setText(weightStr);
            }
        });
        protocalFilter.setPPLockDataInterface(new PPLockDataInterface() {
            @Override
            public void monitorLockData(PPBodyFatModel bodyFatModel, PPDeviceModel deviceModel) {
                if (bodyFatModel.isHeartRateEnd()) {
                    if (bodyFatModel != null) {
                        Logger.d("monitorLockData  bodyFatModel weightKg = " + bodyFatModel.getPpWeightKg());
                    } else {
                        Logger.d("monitorLockData  bodyFatModel heartRate = " + bodyFatModel.getPpHeartRate());
                    }
                    String weightStr = PPUtil.getWeight(unitType, bodyFatModel.getPpWeightKg());
                    if (weightTextView != null) {
                        weightTextView.setText(weightStr);
                        showDialog(deviceModel, bodyFatModel);
                    }
                } else {
                    Logger.d("正在测量心率");
                }
            }
        });

        if (searchType != 0) {
            //Do not receive offline data when binding the device， If you need to receive offline data, please implement this interface
            protocalFilter.setPPHistoryDataInterface(new PPHistoryDataInterface() {
                @Override
                public void monitorHistoryData(PPBodyFatModel bodyBaseModel, boolean isEnd, String dateTime) {
                    if (bodyBaseModel != null) {
                        Logger.d("ppScale_ isEnd = " + isEnd + " dateTime = " + dateTime + " bodyBaseModel weight kg = " + bodyBaseModel.getPpWeightKg());
                    } else {
                        Logger.d("ppScale_ isEnd = " + isEnd);
                    }
                }
            });
        }
        protocalFilter.setUserModel(userModel);
        return protocalFilter;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ppScale.stopSearch();

    }

    private void bindingDevice() {
        if (searchType == 0) {
            ppScale = new PPScale.Builder(this)
                    .setProtocalFilterImpl(getProtocalFilter())
                    .setBleOptions(getBleOptions())
//                    .setDeviceList(null)
                    .setBleStateInterface(bleStateInterface)
                    .build();
            ppScale.startSearchBluetoothScaleWithMacAddressList();
        } else {
            List<DeviceModel> deviceList = DBManager.manager().getDeviceList();
            List<String> addressList = new ArrayList<>();
            for (DeviceModel deviceModel : deviceList) {
                addressList.add(deviceModel.getDeviceMac());
            }
            ppScale = new PPScale.Builder(this)
                    .setProtocalFilterImpl(getProtocalFilter())
                    .setBleOptions(getBleOptions())
                    .setDeviceList(addressList)
                    .setBleStateInterface(bleStateInterface)
                    .build();
            ppScale.startSearchBluetoothScaleWithMacAddressList();
        }

    }

    private void showDialog(final PPDeviceModel deviceModel, final PPBodyFatModel bodyDataModel) {
        String content = "是否保存当前体重" + PPUtil.getWeight(unitType, bodyDataModel.getPpWeightKg());
        if (builder == null) {
            builder = new AlertDialog.Builder(BindingDeviceActivity.this);
        }
        builder.setTitle("这是您的数据么？");
        builder.setMessage(content);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBManager.manager().insertDevice(deviceModel);

                DataUtil.util().setBodyDataModel(bodyDataModel);
                dismissSelf();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                ppScale.reSearchDevice();
                bindingDevice();
            }
        });
        if (!this.isDestroyed() && !this.isFinishing()) {
            if (alertDialog == null || !alertDialog.isShowing()) {
                alertDialog = builder.show();
            }
        }
    }

    private void dismissSelf() {
        ppScale.stopSearch();
        finish();
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
                Toast.makeText(BindingDeviceActivity.this, "系统蓝牙断开", Toast.LENGTH_SHORT).show();
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                Logger.d("系统蓝牙打开");
                Toast.makeText(BindingDeviceActivity.this, "系统蓝牙打开", Toast.LENGTH_SHORT).show();
            } else {
                Logger.e("系统蓝牙异常");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ppScale != null) {
            ppScale.stopSearch();
        }
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
            builder = null;
        }
    }
}


