package com.lefu.ppscale.ble.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import com.lefu.ppscale.ble.R;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiInterface;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPDeviceModel;

public class BleConfigWifiActivity extends AppCompatActivity {

    private PPScale ppScale;
    TextView logCat;
    TextView logCat2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_config_wifi);

        logCat = findViewById(R.id.logCat);
        logCat2 = findViewById(R.id.logCat2);
        ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
        protocalFilter.setConfigWifiInterface(new PPConfigWifiInterface() {

            /**
             * wifi设备配网成功并获取到SN
             *
             * @param sn 设备识别码
             */
            @Override
            public void monitorConfigState(String sn, PPDeviceModel deviceModel) {
                //拿到sn 处理业务逻辑
                Logger.e("xxxxxxxxxxxx-" + sn);
                Logger.e("xxxxxxxxxxxx-deviceName = " + deviceModel.getDeviceName() + " mac = " + deviceModel.getDeviceMac());
                ppScale.stopWifiConfig();
                finish();
            }
        });

        ppScale = new PPScale.Builder(this)
                .setProtocalFilterImpl(protocalFilter)
                .setBleOptions(getBleOptions())
                .setBleStateInterface(bleStateInterface)
                .build();
        ppScale.startSearchBluetoothScaleWithMacAddressList();

    }

    /**
     * 参数配置 绑定时请确保WIFI是2.4G，并且账号密码正确
     *
     * @param password     WIFI密码
     * @param featuresFlag 具备的能力，WIFI秤{@link BleOptions.ScaleFeatures#FEATURES_CONFIG_WIFI}
     *                     具备的能力，体重秤{@link BleOptions.ScaleFeatures#FEATURES_WEIGHT}
     *                     具备的能力，脂肪秤{@link BleOptions.ScaleFeatures#FEATURES_FAT}
     *                     具备的能力，心率秤{@link BleOptions.ScaleFeatures#FEATURES_HEART_RATE}
     *                     具备的能力，离线秤{@link BleOptions.ScaleFeatures#FEATURES_HISTORY}
     *                     具备的能力，闭目单脚秤{@link BleOptions.ScaleFeatures#FEATURES_BMDJ}
     * @return
     * @parm ssid          WIFI账号  不可为空
     */
    private BleOptions getBleOptions() {
        return new BleOptions.Builder()
                .setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_CONFIG_WIFI)
                .setPassword("lefu123456")
                .setSsid("IT36")
//                .setPassword("12345678")
//                .setSsid("Tenda_414A30")
                .build();
    }

    PPBleStateInterface bleStateInterface = new PPBleStateInterface() {
        @Override
        public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel deviceModel) {
            if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                Logger.d(getString(R.string.device_connected));
                setLog(getString(R.string.device_connected));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                Logger.d(getString(R.string.device_connecting));
                setLog(getString(R.string.device_connecting));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                Logger.d(getString(R.string.device_disconnected));
                setLog(getString(R.string.device_disconnected));
            } else if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
                Logger.d(getString(R.string.stop_scanning));
                setLog(getString(R.string.stop_scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                Logger.d(getString(R.string.scan_timeout));
                setLog(getString(R.string.scan_timeout));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                Logger.d(getString(R.string.scanning));
                setLog(getString(R.string.scanning));
            } else {
                Logger.e(getString(R.string.bluetooth_status_is_abnormal));
                setLog(getString(R.string.bluetooth_status_is_abnormal));
            }
        }

        @Override
        public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                Logger.e(getString(R.string.system_bluetooth_disconnect));
                Toast.makeText(BleConfigWifiActivity.this, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show();
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
//                delayScan();
                Logger.d(getString(R.string.system_blutooth_on));
                Toast.makeText(BleConfigWifiActivity.this, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show();
            } else {
                Logger.e(getString(R.string.system_bluetooth_abnormal));
            }
        }

        @Override
        public void monitorLogData(String log) {
            if (log.contains("send")) {
                setLog(log);
            } else {
                setLog2(log);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        ppScale.stopWifiConfig();
        ppScale.disConnect();
        ppScale.stopSearch();
    }

    private void setLog(final String log) {

        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (logCat != null) {
                logCat.append(log + "\n");
            }
        } else {
            if (logCat != null) {
                logCat.post(new Runnable() {
                    @Override
                    public void run() {
                        logCat.append(log + "\n");
                    }
                });
            }
        }
    }

    private void setLog2(final String log) {

        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (logCat2 != null) {
                logCat2.append(log + "\n");
            }
        } else {
            if (logCat2 != null) {
                logCat2.post(new Runnable() {
                    @Override
                    public void run() {
                        logCat2.append(log + "\n");
                    }
                });
            }
        }
    }

}
