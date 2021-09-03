package com.lefu.ppscale.wifi.develop;

import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.lefu.ppscale.wifi.R;
import com.lefu.ppscale.wifi.activity.DeviceListActivity;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiInfoInterface;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPDeviceModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class DeveloperActivity extends AppCompatActivity {

    public static final String ADDRESS = "address";

    @BindView(R.id.iv_Left)
    ImageView iv_Left;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.developer_mode_id_startConnect)
    Button reStartConnectView;
    @BindView(R.id.developer_mode_id_input_modifyServerIP)
    EditText inputServerIP;
    @BindView(R.id.developer_mode_id_input_modifyServerDNS)
    EditText inputServerDNS;
    @BindView(R.id.developer_mode_id_password)
    TextView mPasswordView;
    @BindView(R.id.developer_mode_id_ssid)
    TextView mSsidView;
    @BindView(R.id.developer_mode_id_sn)
    TextView mSnView;

    private PPScale ppScale;
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.developer_activity);

        unbinder = ButterKnife.bind(this);
        initView();
        initData();


    }

    protected void initView() {
        iv_Left.setVisibility(View.VISIBLE);
    }

    protected void initData() {
        startScanBle();
    }

    private void startScanBle() {
        String address = getIntent().getStringExtra(ADDRESS);
        List<String> devices = new ArrayList<>();
        devices.add(address);

        if (ppScale == null) {
            BleOptions bleOptions = new BleOptions.Builder()
                    .setSearchTag(BleOptions.SEARCH_TAG_DIRECT_CONNECT)
                    .build();

            ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();

            protocalFilter.setConfigWifiInfoInterface(ppConfigWifiInfoInterface);

            ppScale = new PPScale
                    .Builder(this)
                    .setBleStateInterface(ppBleStateInterface)
                    .setDeviceList(devices)
                    .setBleOptions(bleOptions)
                    .setProtocalFilterImpl(protocalFilter)
                    .build();
        }

        ppScale.startSearchBluetoothScaleWithMacAddressList();
    }

    PPConfigWifiInfoInterface ppConfigWifiInfoInterface = new PPConfigWifiInfoInterface() {

        @Override
        public void monitorConfigSn(String sn, PPDeviceModel ppDeviceModel) {
            mSnView.setText(sn);
        }

        @Override
        public void monitorConfigSsid(String ssid, PPDeviceModel ppDeviceModel) {
            if (!TextUtils.isEmpty(ssid)) {
                mSsidView.setText(ssid);
            }
        }

        @Override
        public void monitorConfigPassword(String password, PPDeviceModel ppDeviceModel) {
            if (!TextUtils.isEmpty(password)) {
                mPasswordView.setText(password);
            }
        }

        @Override
        public void monitorModifyServerDNSSuccess() {
            Toast.makeText(DeveloperActivity.this, R.string.dns_sent_successfully, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void monitorModifyServerIpSuccess() {
            Toast.makeText(DeveloperActivity.this, R.string.service_ip_sent_successfully, Toast.LENGTH_SHORT).show();
        }
    };

    PPBleStateInterface ppBleStateInterface = new PPBleStateInterface() {

        @Override
        public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel ppDeviceModel) {
            onBleWorkStateChange(ppBleWorkState);
        }

        @Override
        public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
            switch (ppBleSwitchState) {
                case PPBleSwitchStateOn:
                    tvTitle.setText(R.string.system_blutooth_on);
                    break;
                case PPBleSwitchStateOff:
                    tvTitle.setText(R.string.system_bluetooth_disconnect);
                    break;
            }
            reStartConnectView.setVisibility(View.VISIBLE);
        }

        @Override
        public void monitorLogData(String log) {

        }
    };

    private void onBleWorkStateChange(PPBleWorkState ppBleWorkState) {
        if (reStartConnectView != null) {
            reStartConnectView.setVisibility(View.GONE);
            switch (ppBleWorkState) {
                case PPBleStateSearchCanceled:
                    Logger.d(getString(R.string.scan_cancle));

                    tvTitle.setText(getString(R.string.scan_cancle));
                    break;
                case PPBleWorkSearchTimeOut:
                    Logger.d(getString(R.string.scan_time_out));

                    tvTitle.setText(getString(R.string.scan_time_out));
                    break;
                case PPBleWorkStateConnected:
                    Logger.d(getString(R.string.device_connected));

                    tvTitle.setText(getString(R.string.device_connected));
                    break;
                case PPBleWorkStateSearching:
                    Logger.d(getString(R.string.scanning));
                    tvTitle.setText(getString(R.string.scanning));
                    break;
                case PPBleWorkStateConnecting:
                    Logger.d(getString(R.string.device_connecting));
//                    tvTitle.setText(getString(R.string.device_connecting));
                    setLog(getString(R.string.device_connecting), tvTitle);
                    break;
                case PPBleWorkStateDisconnected:
                    Logger.d(getString(R.string.device_disconnected));
//                    tvTitle.setText(getString(R.string.device_disconnected));
                    reStartConnectView.setVisibility(View.VISIBLE);
                    setLog(getString(R.string.device_disconnected), tvTitle);
                    break;
            }
        }
    }


    @OnClick({R.id.iv_Left,
            R.id.developer_mode_id_getSSID,
            R.id.developer_mode_id_modifyServerIP,
            R.id.developer_mode_id_modifyServerDNS,
            R.id.developer_mode_id_startConnect,
            R.id.developer_mode_id_clearSSID,
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_Left:
                finish();
                break;
            case R.id.developer_mode_id_getSSID:
                if (ppScale != null) {
                    ppScale.sendInquityWifiConfig();
                }
                break;
            case R.id.developer_mode_id_modifyServerIP:
                String ip = inputServerIP.getText().toString();
                if (!TextUtils.isEmpty(ip)) {
                    ip = ip.trim();
                    if (ppScale != null) {
                        ppScale.sendModifyServerIp(ip);
                    }
                }
                break;
            case R.id.developer_mode_id_modifyServerDNS:
                String dns = inputServerDNS.getText().toString();
                if (!TextUtils.isEmpty(dns)) {
                    dns = dns.trim();
                    if (ppScale != null) {
                        ppScale.sendModifyServerDNS(dns);
                    }
                }
                break;
            case R.id.developer_mode_id_startConnect:
                startScanBle();
                break;
            case R.id.developer_mode_id_clearSSID:
                if (ppScale != null) {
                    ppScale.sendDeleteWifiConfig();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ppScale != null) {
            ppScale.stopSearch();
            ppScale.disConnect();
        }
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    private void setLog(final String log, TextView logCat) {

        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (logCat != null) {
                logCat.setText(log);
            }
        } else {
            if (logCat != null) {
                logCat.post(new Runnable() {
                    @Override
                    public void run() {
                        logCat.setText(log);
                    }
                });
            }
        }
    }

}
