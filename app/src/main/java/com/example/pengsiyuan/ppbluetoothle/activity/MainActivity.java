package com.example.pengsiyuan.ppbluetoothle.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pengsiyuan.ppbluetoothle.R;
import com.example.pengsiyuan.ppbluetoothle.util.DataUtil;
import com.example.pengsiyuan.ppbluetoothle.util.PPUtil;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.util.UnitUtil;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPUserModel;
import com.peng.ppscale.vo.PPUserSex;


public class MainActivity extends AppCompatActivity {

    TextView weightTextView;
    int height = 180;
    int age = 18;
    PPUnitType unit = PPUnitType.Unit_KG;
    PPUserSex sex = PPUserSex.PPUserSexMale;
    int group = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weightTextView = findViewById(R.id.weightTextView);
        requestPower();

        Button mBtnConfigWifi = findViewById(R.id.wificonfigBtn);
        mBtnConfigWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PPScale.isBluetoothOpened()) {
                    Intent intent = new Intent(MainActivity.this, BleConfigWifiActivity.class);
                    startActivity(intent);
                } else {
                    PPScale.openBluetooth();
                }
            }
        });

        Button mBtnBindingDeice = findViewById(R.id.bindingDeviceBtn);
        mBtnBindingDeice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PPScale.isBluetoothOpened()) {
                    Intent intent = new Intent(MainActivity.this, BindingDeviceActivity.class);
                    intent.putExtra(BindingDeviceActivity.UNIT_TYPE, unit.getType());
                    intent.putExtra(BindingDeviceActivity.SEARCH_TYPE, 0);
                    startActivity(intent);
                } else {
                    PPScale.openBluetooth();
                }
            }
        });

        Button mBtnScaleWeight = findViewById(R.id.scaleWeightBtn);
        mBtnScaleWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showDialog();

                if (PPScale.isBluetoothOpened()) {
                    Intent intent = new Intent(MainActivity.this, BindingDeviceActivity.class);
                    intent.putExtra(BindingDeviceActivity.UNIT_TYPE, unit.getType());
                    intent.putExtra(BindingDeviceActivity.SEARCH_TYPE, 1);
                    startActivity(intent);
                } else {
                    PPScale.openBluetooth();
                }
            }
        });

        Button mBtnDeviceList = findViewById(R.id.deviceManagerBtn);
        mBtnDeviceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivity(intent);
            }
        });

        Button mBtnDataDetail = findViewById(R.id.dataDetailBtn);
        mBtnDataDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BodyDataDetailActivity.class);
                startActivity(intent);
            }
        });


        // 身高
        EditText heightET = findViewById(R.id.editText3);
        heightET.setText(this.height + "");
        heightET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String number = s.toString();
                if (number.length() > 0) {
                    int height = Integer.parseInt(number);
                    MainActivity.this.height = height;
                }
            }
        });

        //年龄
        EditText ageET = findViewById(R.id.editText6);
        ageET.setText(this.age + "");
        ageET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String number = s.toString();
                if (number.length() > 0) {
                    int age = Integer.parseInt(number);
                    MainActivity.this.age = age;
                }
            }
        });
        //单位
        EditText unitET = findViewById(R.id.editText4);
        unitET.setText("0");
        unitET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String number = s.toString();
                if (number.length() > 0) {
                    int unit = Integer.parseInt(number);
                    MainActivity.this.unit = UnitUtil.getUnitType(unit);
                }
            }
        });

        //性别
        EditText sexET = findViewById(R.id.editText5);
        sexET.setText("0");
        sexET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String number = s.toString();
                if (number.length() > 0) {
                    int sex = Integer.parseInt(number);
                    if (sex == 0) {
                        MainActivity.this.sex = PPUserSex.PPUserSexFemal;
                    } else {
                        MainActivity.this.sex = PPUserSex.PPUserSexMale;
                    }
                }

            }
        });

        //用户组
        EditText groupET = findViewById(R.id.editText7);
        groupET.setText("0");
        groupET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String number = s.toString();
                if (number.length() > 0) {
                    int group = Integer.parseInt(number);
                    MainActivity.this.group = group;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PPBodyFatModel bodyData = DataUtil.util().getBodyDataModel();

        if (bodyData != null) {
            String weightStr = PPUtil.getWeight(this.unit, bodyData.getPpWeightKg());
            weightTextView.setText(getString(R.string.body_weight_) + weightStr);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PPUserModel userModel = new PPUserModel.Builder()
                .setAge(this.age)
                .setHeight(this.height)
                .setGroupNum(this.group)
                .setSex(this.sex)
                .build();
        DataUtil.util().setUserModel(userModel);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void requestPower() {
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1);
            }
        }
    }

    private void showDialog() {
        String content = getString(R.string.please_select_function);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(content);
        builder.setPositiveButton(R.string.bmdj, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, BMDJConnectActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.measure_weight, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (PPScale.isBluetoothOpened()) {
                    Intent intent = new Intent(MainActivity.this, BindingDeviceActivity.class);
                    intent.putExtra(BindingDeviceActivity.UNIT_TYPE, unit.getType());
                    intent.putExtra(BindingDeviceActivity.SEARCH_TYPE, 1);
                    startActivity(intent);
                } else {
                    PPScale.openBluetooth();
                }
            }
        });
        builder.show();
    }


}
