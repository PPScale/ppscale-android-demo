package com.example.pengsiyuan.ppbluetoothle.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pengsiyuan.ppbluetoothle.R;
import com.example.pengsiyuan.ppbluetoothle.util.DataUtil;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.babyhold.PPBabyHoldInterface;
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiInterface;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.PPLockDataInterface;
import com.peng.ppscale.business.ble.listener.PPProcessDateInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPBodyBaseModel;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPDeviceModel;

/**
 * Baby weighing
 */
public class BabyHoldScaleActivity extends AppCompatActivity {

    private PPScale ppScale;
    private TextView weightTextView;
    private TextView textView7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_weight);

        initView();

        ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
        protocalFilter.setPPProcessDateInterface(new PPProcessDateInterface() {
            @Override
            public void monitorProcessData(PPBodyBaseModel bodyBaseModel) {
                Logger.d(" progress weight = " + bodyBaseModel.getPpWeightKg());
            }
        });
        protocalFilter.setPPLockDataInterface(new PPLockDataInterface() {
            @Override
            public void monitorLockData(final PPBodyFatModel ppBodyFatModel, PPDeviceModel deviceModel) {

                /**
                 * 婴儿体重会在大人体重之后返回，必须先秤大人再秤大人抱小孩
                 */
                Logger.d(" lock weight = " + ppBodyFatModel.getPpWeightKg());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView7.setText("当前称重");
                        weightTextView.setText(String.valueOf(ppBodyFatModel.getPpWeightKg()));
                    }
                });
            }
        });


        /*
        @Deprecated 已过时，请勿使用
        protocalFilter.setBabyHoldInterface(new PPBabyHoldInterface() {
            @Override
            public void monitorFisrtData(final PPBodyFatModel bodyFatModel, PPDeviceModel deviceModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView7.setText("第二次称重");
                        weightTextView.setText(String.valueOf(bodyFatModel.getPpWeightKg()));
                    }
                });
            }

            @Override
            public void monitorSecondData(final PPBodyFatModel bodyFatModel, PPDeviceModel deviceModel, final double babyWeight) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView7.setText("婴儿体重");
                        weightTextView.setText(String.valueOf(bodyFatModel.getPpWeightKg()));
                        bodyFatModel.setPpWeightKg(babyWeight);

                        DataUtil.util().setBodyDataModel(bodyFatModel);
                        finish();
                    }
                });
            }

            @Override
            public void monitorProcessData(final PPBodyFatModel bodyFatModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weightTextView.setText(String.valueOf(bodyFatModel.getPpWeightKg()));
                    }
                });
            }
        });*/
        ppScale = new PPScale.Builder(this)
                .setProtocalFilterImpl(protocalFilter)
                .setBleOptions(getBleOptions())
                .setBleStateInterface(bleStateInterface)
                .build();
        ppScale.startSearchBluetoothScaleWithMacAddressList();

    }

    private void initView() {
        weightTextView = findViewById(R.id.weightTextView);
        textView7 = findViewById(R.id.textView7);
        textView7.setText("第一次称重");
    }

    /**
     * 搜索设备参数配置
     *
     * @param featuresFlag 具备的能力，WIFI秤{@link BleOptions.ScaleFeatures#FEATURES_CONFIG_WIFI}
     *                     具备的能力，体重秤{@link BleOptions.ScaleFeatures#FEATURES_WEIGHT}
     *                     具备的能力，脂肪秤{@link BleOptions.ScaleFeatures#FEATURES_FAT}
     *                     具备的能力，心率秤{@link BleOptions.ScaleFeatures#FEATURES_HEART_RATE}
     *                     具备的能力，离线秤{@link BleOptions.ScaleFeatures#FEATURES_HISTORY}
     *                     具备的能力，闭目单脚秤{@link BleOptions.ScaleFeatures#FEATURES_BMDJ}
     *                     具备的能力，抱婴称重{@link BleOptions.ScaleFeatures#FEATURES_HOLD_BABY}
     * @return
     */
    private BleOptions getBleOptions() {
        return new BleOptions.Builder()
                .setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_HOLD_BABY)
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
                Toast.makeText(BabyHoldScaleActivity.this, "系统蓝牙断开", Toast.LENGTH_SHORT).show();
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                Logger.d("系统蓝牙打开");
                Toast.makeText(BabyHoldScaleActivity.this, "系统蓝牙打开", Toast.LENGTH_SHORT).show();
            } else {
                Logger.e("系统蓝牙异常");
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        //断开蓝牙或退出抱婴模式，请调用disConnect
        ppScale.disConnect();
        ppScale.stopSearch();
    }
}
