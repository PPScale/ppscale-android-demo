package com.lefu.ppscale.wifi.data;

import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPUserModel;

import java.io.Serializable;

public class BodyFataDataModel extends PPBodyFatModel implements Serializable {
    public BodyFataDataModel(double ppWeightKg, String scaleType, PPUserModel userModel, String scaleName) {
        super(ppWeightKg, scaleType, userModel, scaleName);
    }

    public BodyFataDataModel(double ppWeightKg, String scaleType, String scaleName, PPUnitType unitType) {
        super(ppWeightKg, scaleType, scaleName, unitType);
    }

    public BodyFataDataModel(double ppWeightKg, int impedance, String scaleType, PPUserModel userModel, String scaleName) {
        super(ppWeightKg, impedance, scaleType, userModel, scaleName);
    }

    public BodyFataDataModel(double ppWeightKg, int impedance, String scaleType, PPUserModel userModel, String scaleName, PPUnitType unitType) {
        super(ppWeightKg, impedance, scaleType, userModel, scaleName, unitType);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
