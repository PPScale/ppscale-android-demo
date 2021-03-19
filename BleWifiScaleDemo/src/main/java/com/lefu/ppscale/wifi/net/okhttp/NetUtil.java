package com.lefu.ppscale.wifi.net.okhttp;

import com.lefu.ppscale.wifi.BuildConfig;

public class NetUtil {

    /***前面的链接地址***/
    public static String GET_URL() {
        String url = null;
        if (!BuildConfig.DEBUG) {
            url = "https://api.lefuenergy.com";   // 线上正式服务器
        } else {
            url = "http://192.168.8.197:6032/";   // 内网测试服务器
        }
        return url;
    }

    //清除设备数据
    public static String CLEAR_DEVICE_DATA = GET_URL() + "/lefu/wifi/app/clearDeviceData";
    //获取秤端上传的体重信息（可根据sn号和uid单独查询 或 组合查询）
    public static String GET_SCALE_WEIGHTS = GET_URL() + "/lefu/wifi/app/getScaleWeights";
    //保存用户wifi组
    public static String SAVE_WIFI_GROUP = GET_URL() + "/lefu/wifi/app/saveWifiGroup";






}
