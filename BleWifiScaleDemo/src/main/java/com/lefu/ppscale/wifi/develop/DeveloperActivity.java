package com.lefu.ppscale.wifi.develop;

import android.os.Bundle;
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
            Toast.makeText(DeveloperActivity.this, "发送DNS成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void monitorModifyServerIpSuccess() {
            Toast.makeText(DeveloperActivity.this, "发送SeviceIp成功", Toast.LENGTH_SHORT).show();
        }
    };

    PPBleStateInterface ppBleStateInterface = new PPBleStateInterface() {

        @Override
        public void monitorBluetoothWorkState(final PPBleWorkState ppBleWorkState) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onBleWorkStateChange(ppBleWorkState);
                }
            });
        }

        @Override
        public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
            switch (ppBleSwitchState) {
                case PPBleSwitchStateOn:
                    tvTitle.setText("系统蓝牙打开");
                    break;
                case PPBleSwitchStateOff:
                    tvTitle.setText("系统蓝牙关闭");
                    break;
            }
            reStartConnectView.setVisibility(View.VISIBLE);
        }
    };

    private void onBleWorkStateChange(PPBleWorkState ppBleWorkState) {
        if (reStartConnectView != null) {
            reStartConnectView.setVisibility(View.GONE);
            switch (ppBleWorkState) {
                case PPBleStateSearchCanceled:
                    Logger.d("取消扫描");

                    tvTitle.setText("扫描");
                    break;
                case PPBleWorkSearchTimeOut:
                    Logger.d("扫描超时");

                    tvTitle.setText("扫描超时");
                    break;
                case PPBleWorkStateConnected:
                    Logger.d("设备已连接");

                    tvTitle.setText("设备已连接");
                    break;
                case PPBleWorkStateSearching:
                    Logger.d("扫描中");
                    tvTitle.setText("扫描中");
                    break;
                case PPBleWorkStateConnecting:
                    Logger.d("设备连接中");
                    tvTitle.setText("设备连接中");
                    break;
                case PPBleWorkStateDisconnected:
                    Logger.d("设备已断开");
                    tvTitle.setText("设备已断开");
                    reStartConnectView.setVisibility(View.VISIBLE);
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
}
