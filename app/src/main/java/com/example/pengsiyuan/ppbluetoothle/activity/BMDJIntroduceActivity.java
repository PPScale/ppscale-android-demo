package com.example.pengsiyuan.ppbluetoothle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.pengsiyuan.ppbluetoothle.R;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.business.ble.bmdj.PPBMDJStatesInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.util.Logger;

public class BMDJIntroduceActivity extends AppCompatActivity {

    private boolean isAutoPush;

    PPScale ppScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.isAutoPush = false;
        setContentView(R.layout.activity_bmdjintroduce);

        ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();

        protocalFilter.setBmdjStatesInterface(new PPBMDJStatesInterface() {

            @Override
            public void startTiming() {
                isAutoPush = true;
                Intent intent = new Intent(BMDJIntroduceActivity.this, BMDJTimeActivity.class);
                startActivity(intent);
            }

            @Override
            public void monitorBMDJExitSuccess() {
                //断开闭目单脚
                ppScale.exitBMDJModel();
            }

            @Override
            public void monitorBMDJExittFail() {
                Logger.e("monitorBMDJExittFail");
            }

        });

        BleOptions bleOptions = new BleOptions.Builder()
                .setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_BMDJ)
                .setUnitType(PPUnitType.Unit_KG)
                .build();

        ppScale = new PPScale.Builder(this)
                .setProtocalFilterImpl(protocalFilter)
                .setBleOptions(bleOptions)
//                .setBleStateInterface(bleStateInterface)
                .build();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAutoPush) {
            ppScale.exitBMDJModel();
        }
    }


}
