package com.lefu.ppscale.wifi.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.lefu.healthu.business.mine.binddevice.BindDeviceWiFiLockSelectConfigNetDialog;
import com.lefu.ppscale.wifi.DBManager;
import com.lefu.ppscale.wifi.R;
import com.lefu.ppscale.wifi.model.DeviceModel;
import com.lefu.ppscale.wifi.util.DataUtil;
import com.lefu.ppscale.wifi.util.PPUtil;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.PPDeviceInfoInterface;
import com.peng.ppscale.business.ble.listener.PPHistoryDataInterface;
import com.peng.ppscale.business.ble.listener.PPLockDataInterface;
import com.peng.ppscale.business.ble.listener.PPProcessDateInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.device.PPDeviceType;
import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPBodyBaseModel;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPUserModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BindingDeviceActivity extends FragmentActivity {

    private static final String BUNDLE_FRAGMENTS_KEY = BindDeviceWiFiLockSelectConfigNetDialog.class.getSimpleName();

    TextView weightTextView;
    PPScale ppScale;

    private BindDeviceWiFiLockSelectConfigNetDialog configWifiDialog;

    /**
     * PPUnitKG = 0,
     * PPUnitLB = 1,
     * PPUnitST = 2,
     * PPUnitJin = 3,
     * PPUnitG = 4,
     * PPUnitLBOZ = 5,
     * PPUnitOZ = 6,
     * PPUnitMLWater = 7,
     * PPUnitMLMilk = 8,
     */
    public static final String UNIT_TYPE = "unitType";
    //0??????????????? 1?????????????????????
    public static final String SEARCH_TYPE = "SearchType";

    private PPUnitType unitType;
    private PPUserModel userModel;
    private int searchType;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    private boolean isStepOne;
    private PPBodyFatModel mbodyDataModel;

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

    /**
     * ????????????
     * <p>
     *
     * @param ScaleFeatures ?????????????????????????????????????????????????????????????????????????????????
     *                      ??????????????????
     *                      ?????????{@link BleOptions.ScaleFeatures#FEATURES_WEIGHT}
     *                      ?????????{@link BleOptions.ScaleFeatures#FEATURES_FAT}
     *                      ?????????{@link BleOptions.ScaleFeatures#FEATURES_HEART_RATE}
     *                      ?????????{@link BleOptions.ScaleFeatures#FEATURES_HISTORY}
     *                      ???????????????{@link BleOptions.ScaleFeatures#FEATURES_BMDJ}
     *                      ????????????{@link BleOptions.ScaleFeatures#FEATURES_CALCUTE_IN_SCALE}
     *                      WIFI???{@link BleOptions.ScaleFeatures#FEATURES_CONFIG_WIFI} ?????????{@link BleConfigWifiActivity}
     *                      ?????????{@link BleOptions.ScaleFeatures#FEATURES_FOOD_SCALE}
     *                      ???????????????{@link BleOptions.ScaleFeatures#FEATURES_NORMAL}  //??????????????????
     *                      ?????????{@link BleOptions.ScaleFeatures#FEATURES_ALL}
     *                      ?????????{@link BleOptions.ScaleFeatures#FEATURES_CUSTORM} //???????????????????????????PPScale???setDeviceList()
     * @return
     * @parm unitType ?????????????????????????????????
     */
    private BleOptions getBleOptions() {
        return new BleOptions.Builder()
                .setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_NORMAL)
                .setUnitType(unitType)
                .setSearchTag(BleOptions.SEARCH_TAG_NORMAL) //If you need to connect directly, please switch to BleOptions.SEARCH_TAG_DIRECT_CONNECT
                .build();
    }

    /**
     * ?????????????????? Parse data callback
     *
     * @return
     */
    private ProtocalFilterImpl getProtocalFilter() {
        ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
        protocalFilter.setPPProcessDateInterface(new PPProcessDateInterface() {
            /**
             * ????????????  Process data
             * @param bodyBaseModel
             * @param deviceModel
             */
            @Override
            public void monitorProcessData(PPBodyBaseModel bodyBaseModel, PPDeviceModel deviceModel) {
                Logger.d("bodyBaseModel scaleName " + bodyBaseModel.getScaleName());
                String weightStr = PPUtil.getWeight(bodyBaseModel.getUnit(), bodyBaseModel.getPpWeightKg());
                weightTextView.setText(weightStr);
            }
        });
        protocalFilter.setPPLockDataInterface(new PPLockDataInterface() {
            /**
             * ????????????  Lock data
             *
             * @param bodyFatModel
             * @param deviceModel
             */
            @Override
            public void monitorLockData(PPBodyFatModel bodyFatModel, PPDeviceModel deviceModel) {
                if (bodyFatModel.isHeartRateEnd()) {
                    if (bodyFatModel != null) {
                        Logger.d("monitorLockData  bodyFatModel weightKg = " + bodyFatModel.getPpWeightKg());
                    } else {
                        Logger.d("monitorLockData  bodyFatModel heartRate = " + bodyFatModel.getPpHeartRate());
                    }
                    mbodyDataModel = bodyFatModel;
                    String weightStr = PPUtil.getWeight(bodyFatModel.getUnit(), bodyFatModel.getPpWeightKg());
                    if (weightTextView != null) {
                        weightTextView.setText(weightStr);
                    }
                    if (PPDeviceType.Scale.isConfigWifiScale(deviceModel.getDeviceName())) {
                        //Bluetooth WiFi scale
                        showWiFiConfigDialog(weightStr, deviceModel);
                    } else {
                        //Ordinary bluetooth scale
                        showDialog(deviceModel, bodyFatModel);
                    }
                } else {
                    //??????????????????????????????????????????  If you don???t have a heart rate scale, you don???t have to deal with it here
                    Logger.d("Heart rate is being measured");
                }
            }
        });

        protocalFilter.setDeviceInfoInterface(new PPDeviceInfoInterface() {
            @Override
            public void softwareRevision(PPDeviceModel deviceModel) {
                Logger.d("Scale Ver???" + deviceModel.getFirmwareVersion());
            }

            @Override
            public void batteryPower(PPDeviceModel deviceModel) {
                Logger.d("Scale Electricity???" + deviceModel.getBatteryPower());
            }
        });

        if (searchType != 0) {
            //Do not receive offline data when binding the device??? If you need to receive offline data, please implement this interface
            protocalFilter.setPPHistoryDataInterface(new PPHistoryDataInterface() {
                /**
                 * historical data
                 *
                 * @param bodyBaseModel
                 * @param isEnd
                 * @param dateTime
                 */
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
        return protocalFilter;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ppScale.stopSearch();

    }

    private void bindingDevice() {
        if (searchType == 0) {
            //???????????????
            ppScale = new PPScale.Builder(this)
                    .setProtocalFilterImpl(getProtocalFilter())
                    .setBleOptions(getBleOptions())
//                    .setDeviceList(null)
                    .setUserModel(userModel)
                    .setBleStateInterface(bleStateInterface)
                    .build();
            ppScale.startSearchBluetoothScaleWithMacAddressList(30 * 1000);
        } else {
            //??????????????????
            List<DeviceModel> deviceList = DBManager.manager().getDeviceList();
            List<String> addressList = new ArrayList<>();
            for (DeviceModel deviceModel : deviceList) {
                addressList.add(deviceModel.getDeviceMac());
            }
            ppScale = new PPScale.Builder(this)
                    .setProtocalFilterImpl(getProtocalFilter())
                    .setBleOptions(getBleOptions())
                    .setDeviceList(addressList)
                    .setUserModel(userModel)
                    .setBleStateInterface(bleStateInterface)
                    .build();
            ppScale.startSearchBluetoothScaleWithMacAddressList(30 * 1000);
        }
    }

    private void showDialog(final PPDeviceModel deviceModel, final PPBodyFatModel bodyDataModel) {
        String content = getString(R.string.whether_to_save_the_) + PPUtil.getWeight(bodyDataModel.getUnit(), bodyDataModel.getPpWeightKg());
        if (builder == null) {
            builder = new AlertDialog.Builder(BindingDeviceActivity.this);
        }
        builder.setTitle(R.string.is_this_your_data);
        builder.setMessage(content);
        builder.setPositiveButton(R.string.corfirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBManager.manager().insertDevice(deviceModel);

                DataUtil.util().setBodyDataModel(bodyDataModel);
                dismissSelf();
            }
        });
        builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
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

    PPBleStateInterface bleStateInterface = new PPBleStateInterface() {

        @Override
        public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel ppDeviceModel) {
            if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                Logger.d(getString(R.string.device_connected));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                Logger.d(getString(R.string.device_connecting));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                Logger.d(getString(R.string.device_disconnected));
            } else if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
                Logger.d(getString(R.string.stop_scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                Logger.d(getString(R.string.scan_time_out));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                Logger.d(getString(R.string.scanning));
            } else {
                Logger.e(getString(R.string.bluetooth_status_is_abnormal));
            }
        }

        @Override
        public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                Logger.e(getString(R.string.system_bluetooth_disconnect));
                Toast.makeText(BindingDeviceActivity.this, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show();
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                Logger.d(getString(R.string.system_blutooth_on));
                Toast.makeText(BindingDeviceActivity.this, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show();
            } else {
                Logger.e(getString(R.string.system_bluetooth_abnormal));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disConnect();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
            builder = null;
        }
    }

    private void showWiFiConfigDialog(String weightStr, final PPDeviceModel deviceModel) {
        if (configWifiDialog == null) {
            configWifiDialog = new BindDeviceWiFiLockSelectConfigNetDialog();
        }
        try {
            Bundle bundle = new Bundle();
            bundle.putString("content", weightStr);
            configWifiDialog.setArguments(bundle);

            if (!configWifiDialog.isAdded() && !configWifiDialog.isVisible() && !configWifiDialog.isRemoving()) {
                configWifiDialog.show(getSupportFragmentManager().beginTransaction(), BUNDLE_FRAGMENTS_KEY);
                configWifiDialog.setOnSelectListener(new BindDeviceWiFiLockSelectConfigNetDialog.OnSelectListener() {

                    @Override
                    public void onGoToConfigWiFi(@NotNull DialogFragment dialog) {
                        dialog.dismiss();
                        isStepOne = false;
                        saveDeviceAndBodyFat(deviceModel, mbodyDataModel);
//                        UnitMatchTypeHelper.setDeviceMac(deviceModel.getDeviceMac());

                        if (PPScale.isBluetoothOpened()) {
                            Intent intent = new Intent(BindingDeviceActivity.this, BleConfigWifiActivity.class);
                            intent.putExtra("address", deviceModel.getDeviceMac());
                            startActivity(intent);
                            finish();
                        } else {
                            PPScale.openBluetooth();
                        }

                    }

                    @Override
                    public void onCorfirm(@NotNull DialogFragment dialog) {
                        saveDeviceAndBodyFat(deviceModel, mbodyDataModel);
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onCancle(@NotNull DialogFragment dialog) {
                        startSearchDevice();
                        dialog.dismiss();
                    }

                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSearchDevice() {
        if (ppScale != null) {
            ppScale.disConnect();
            ppScale.startSearchBluetoothScaleWithMacAddressList(false);
        }
    }

    private void saveDeviceAndBodyFat(PPDeviceModel deviceModel, PPBodyFatModel bodyDataModel) {
        DBManager.manager().insertDevice(deviceModel);

        DataUtil.util().setBodyDataModel(bodyDataModel);
        disConnect();
    }

    private void disConnect() {
        if (ppScale != null) {
            ppScale.stopSearch();
            ppScale.disConnect();
        }
    }

    private void dismissSelf() {
        ppScale.stopSearch();
        finish();
    }

}


