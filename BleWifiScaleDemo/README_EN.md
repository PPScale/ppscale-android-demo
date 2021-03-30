
### 1. Introduction
#### 1.1 Operating environment
- The iOS version is 9.0 above, and it needs to be run on a real machine.
- The Android version is 5.0 above, and it needs to be run on a real machine.
--- 
#### 1.2  Some name conventions
- Bluetooth WiFi scale: Bluetooth WiFi dual mode, can transmit data through Bluetooth, can connect to the cloud server through the router for data exchange after the network configuration is completed.
- Weighing scale and body fat scale: The weighing scale only has the function to measure weight. In addition to measuring weight, the body fat scale can also calculate other body parameters by measuring the resistance between the feet.
- Lock data: After standing on the scale surface steadily, the number displayed on the scale surface will tend to a fixed value, and the reading displayed after 3 flashes is the locked data.
- Fat measurement process: the scale screen will have a flat line pattern that changes from top to bottom.
- Fat measurement data: If you are using a body fat scale, the scale will take the initiative to measure the fat after the data is locked, and the fat measurement data will be obtained after this process is completed.
- Fat test failed: If you are using a body fat scale, but you do not see the fat test process during the measurement, please make sure that you are standing with bare feett and the soles of your feet are touching the electrodes.
- Device network configuration: the process of issuing router account and password information via Bluetooth.

#### 1.3 Need permission
In Android 6.0 and above system versions, make sure to enable it before starting the scan
     1. Positioning permissions
     2. Positioning switch
     3. Bluetooth switch
     
```
     <!-- Access to the network, network positioning requires Internet access-->
     <uses-permission android:name="android.permission.INTERNET" />
     <!-- wifi status, used during wifi configuration-->
     <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
     <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />

```
    
Add the following permissions to the manifest file

### 2. Scenarios implemented in the BleWifiScaleDemo 

#### 2.1 If you are purchasing a Bluetooth scale, then your business process may need to include the following parts

##### 2.1.1 User information (corresponding to "UserModel.java" in DEMO)

- Need to provide the user's height, age, gender. These information are necessary when calculating human body parameters. If the device you order is a weight scale instead of a body fat scale, these information are not necessary.
- If your device is a Bluetooth WiFi scale, you need to set the Base Url. This value is the same as the firmware burned on the scale. The purpose is to ensure that the scale and App are accessing the same server.

##### Device binding (corresponding to "BindingDeviceActivity.java" in DEMO)
- If you want to manage the device, you can add this logic. In this process, it will scan all nearby Bluetooth scale devices produced by Unique Scales company. Once the device found, it will try to connect and obtain the data sent by the scale. After receiving the locked data, the device information will be stored locally.

##### 2.1.2 Bluetooth weighing (corresponding to "BindingDeviceActivity.java" in DEMO)

- In this process, only attempts to connect the device  that has been stored locally (the filter condition uses the MAC address of the Bluetooth scale)
--- 

#### 2.2 If you are purchasing a Bluetooth Wifi scale, then the process may need to include the following parts on the basis of the Bluetooth scale

##### Network configuration (corresponding to "BleConfigWifiActivity.java" in DEMO)

- After receiving the lock data, if your device is a Bluetooth WiFi scale (logically judged by the device name), it will enter the device network configuration process. In this process, try to connect to a device with a specific MAC address. After the connection is successful, the router's ssid and password are sent to the device via Bluetooth. The password can be empty. It should be noted that the matched router cannot be in the 5G frequency band, which will cause network configuration failure
- 
##### 2.2.2 Wifi data list (corresponding to "WifiDataListActivity.java" in DEMO)

- The configured Bluetooth WiFi scale is weighed without Bluetooth weighing, and the data will be uploaded to the designated cloud server after the data is locked. In this process, all the weight data uploaded to the server by this scale is obtained through the device's SN (the unique identifier of the WiFi device).
--- 

#### 2.3 Public page

##### 2.3.1 Body details (corresponding to "BodyDataDetailActivity.java" in DEMO)

- After Bluetooth weighing, it will jump to this page of the body details.
- Click the data on the "WiFi data list" page to jump to this page, use the current user information, and bring it into the calculation method to get the body details.
##### 2.3.2 Equipment list (corresponding to "DeviceListActivity.java" in DEMO)

- The list shows the devices recorded in "Device binding". Can unbind and configure the network (if it is a Bluetooth WiFi scale);
- The unbound device needs to be bound again, otherwise it cannot be used on the Bluetooth weighing page.
--- 
### 3. Introduce PPScale to the project, gradle Automatic import method

1、Add build.gradle in the project directory
```
    allprojects {
        repositories {
            、、、
             maven { url 'http://nexus.lefuenergy.com/repository/maven-public' }
        }
    }
```
2、Add to build.gradle under the module that needs to import the SDK //Please use different artifactId according to different branches，The format is: ppscale-branch name // The following is the integration method of the master branch, the corresponding so file has been integrated
    
```
    dependencies {
        、、、
        implementation 'com.peng.ppscale:ppscale-new-master:0.0.4.10'     
    }
```

--- 
### 4. Methods provided by PPScale

#### 4.1  Weight scale and body fat scale related methods

详情请参阅[蓝牙体重、体脂秤的文档](../README.md)
[Bluetooth weight and body fat scale documentation](../doc/README-en.md)

#### 4.2 Related methods of WiFi configuration

Use PPScale's instance object to call the method of scanning nearby devices to search for nearby closed-eye single-leg Bluetooth scales and connect.
```
    /**
     * 参数配置 绑定时请确保WIFI是2.4G，并且账号密码正确
     *
     * @param password     WIFI密码
     * @parm ssid          WIFI账号  不可为空
     */
    private BleOptions getBleOptions() {
        ssid = etWifiName.getText().toString();
        return new BleOptions.Builder()
                .setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_CONFIG_WIFI)
                .setPassword(etWifiKey.getText().toString())
                .setSsid(ssid)
                .build();
    }
```

Complete monitoring of the distribution network
```
      ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
      protocalFilter.setConfigWifiInterface(new PPConfigWifiInterface())
```
Start distribution network
```
    List<String> devices = new ArrayList<>();
            devices.add(address);
            ppScale = new PPScale.Builder(this)
                    .setProtocalFilterImpl(protocalFilter)
                    .setBleOptions(getBleOptions())
                    .setDeviceList(devices)
                    .setBleStateInterface(bleStateInterface)
                    .build();
            ppScale.startSearchBluetoothScaleWithMacAddressList();
```
--- 

#### 4.3  Status monitoring agent method
Bluetooth status monitoring   
```
PPBleStateInterface bleStateInterface = new PPBleStateInterface() {
    @Override
    public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState) {
        if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
            Logger.d("Device connected");
        } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
            Logger.d("Device connecting");
        } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
            Logger.d("Device disconnected");
        } else if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
            Logger.d("Stop scanning");
        } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
            Logger.d("Scan timeout");
        } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
            Logger.d("Scanning");
        } else {
            Logger.e("Bluetooth status is abnormal");
        }
    }

    @Override
    public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
        if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
            Logger.e("System Bluetooth is disconnected");
            Toast.makeText(BleConfigWifiActivity.this, "系统蓝牙断开", Toast.LENGTH_SHORT).show();
        } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
            Logger.d("System Bluetooth is on");
            Toast.makeText(BleConfigWifiActivity.this, "系统蓝牙打开", Toast.LENGTH_SHORT).show();
        } else {
            Logger.e("System Bluetooth status is abnormal");
        }
    }
};

```

--- 
### 5.0 Developer mode
On the device list page, click Settings to enter the developer mode page.
Developer mode is provided, you need to ensure that the scale is always on  
```
    1. Get the sn, ssid and password functions of the scale terminal
    2. Modify the network IP address (IP+Host) of the scale terminal, for example:
    3. Modifying the network DNS address (domain name) of the scale terminal is in conflict with modifying the IP. Only one of them can be set. It is recommended to set DNS
    4. Clear the SSID, the scale will be restored to factory settings
    5. Reconnect is used when the scale is disconnected from the Bluetooth of the mobile phone
```

--- 

### 6. Common problems of Bluetooth WiFi scales

#### 6.1 Why can only the 2.4G frequency be used in the configuration of network?


- It is caused by the hardware specifications. The WiFi chip we use supports only 2.4G frequency.


#### 6.2 Can a mixed-mode router be used for network configuration?

- Yes, the mixed mode of 2.4G and 5G is actually a router signal that constantly switches between 2.4G and 5G. Since the switching frequency is very fast, both frequency bands can be used at the same time. The scale can perform network configuration when the 2.4G frequency band is found. (Provides a network configuration compatibility report, all routers are tested in the default mode)

#### 6.3 When do the scale upload data via WiFi?

- After each measurement is done and data locked.

#### 6.4 How is the historical data of the scale terminal generated?

- After the weight is locked, under the circumstances of Bluetooth is not connected and the server uploading is not successful (the upload failure and the network are not configured), the current lock data of this group of weight will be stored as a history and stored in the scale body (up to Save 100 groups, and use the first-in-first-out principle after exceeding).

#### 6.5 How is the historical data of the scale transmitted?

- Data uploading is divided into two situations: the first is through Bluetooth, the current scale is not connected to the router. If the App connects to the scale via Bluetooth and sends a command to obtain historical data, the scale will store the history The data is notified to the App via Bluetooth and will not be actively deleted after the transfer is completed. You need to wait for the App to issue a delete command, and all historical data will be erased. The second one is through WiFi, the current scale has been equipped with the network before, but the data cannot be uploaded in time for some reason, then after the next measurement of the locked data, it will try to connect to the matching router and upload this weight and all historical data.


#### 6.6 What is the process of network configuration and data upload for App and Bluetooth WiFi scale?

```
sequenceDiagram
App->>Scale: Scan for nearby devices
Scale->>App: Send out Bluetooth broadcast
App->>Scale: Discover the target device and start to connect
Scale->>App: successful connection
App->>Scale:  Send SSID, Password
Scale->>App: Reply to SN
Scale->>Server:Send SN
Server->>Scale:SN is stored
App->>Server: Send SN, UID
Server->>App: Bind SN and UID
Server->>App:  successful network configuration
Scale->>Server:Report weighing data（SN,Weight,impedance）
Server->>App:Find the matching UID through SN, and send the weighing data (Weight, impedance)

```

#### 6.7 What is the process of OTA?

 ```
sequenceDiagram
Scale->>Server: Check the latest firmware version (upload the version number of the local firmware as a parameter)

Server->>Scale: Check if it needs to be upgraded

Server->>Scale: If it needs to be upgraded (return to the download path of the firmware)

Scale->>Server: Download from the firmware download path

Scale->>Server: PASS is shown after the upgrade is successful

```

Contact Developer：
Email: yanfabu-5@lefu.cc


