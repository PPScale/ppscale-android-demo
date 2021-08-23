package com.lefu.ppscale.ble.model;

import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPUserModel;
import com.peng.ppscale.vo.PPUserSex;

import java.util.List;

public class DataUtil {
    private static DataUtil dataUtil;
    private PPBodyFatModel bodyDataModel;
    private PPUserModel userModel;
    List<PPBodyFatModel> bodyFatModels;

    public static DataUtil util() {
        if (dataUtil == null) {
            synchronized (DataUtil.class) {
                if (dataUtil == null) {
                    dataUtil = new DataUtil();
                }
            }
        }
        return dataUtil;
    }

    public DataUtil() {
        userModel = new PPUserModel.Builder().setAge(18)
                .setHeight(180)
                .setSex(PPUserSex.PPUserSexMale)
                .setGroupNum(0)
                .build();
    }

    public PPBodyFatModel getBodyDataModel() {
        return bodyDataModel;
    }

    public void setBodyDataModel(PPBodyFatModel bodyDataModel) {
        this.bodyDataModel = bodyDataModel;
    }

    public void setHistoryData(List<PPBodyFatModel> bodyFatModels) {
        this.bodyFatModels = bodyFatModels;
    }

    public List<PPBodyFatModel> getHistoryData() {
        return bodyFatModels;
    }

    public PPUserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(PPUserModel userModel) {
        this.userModel = userModel;
    }
}
