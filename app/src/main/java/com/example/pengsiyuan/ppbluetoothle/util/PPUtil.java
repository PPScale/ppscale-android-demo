package com.example.pengsiyuan.ppbluetoothle.util;

import com.peng.ppscale.business.device.DeviceManager;
import com.peng.ppscale.business.device.PPUnitType;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class PPUtil {

    public static String kgToLB_ForFatScale(double tempKg) {
        if (0 == tempKg) return "0.0";
        BigDecimal b0 = new BigDecimal(String.valueOf(tempKg));
        BigDecimal b1 = new BigDecimal("1155845");
        BigDecimal b2 = new BigDecimal("16");
        BigDecimal b4 = new BigDecimal("65536");
        BigDecimal b5 = new BigDecimal("2");
        BigDecimal b3 = new BigDecimal(String.valueOf(b0.multiply(b1).doubleValue())).divide(b2, 5, BigDecimal.ROUND_HALF_EVEN).divide(b4, 1, BigDecimal.ROUND_HALF_UP);
        float templb = b3.multiply(b5).floatValue();
        return String.valueOf(templb);
    }

    public static String kgToJin(double lb) {
        double kg = lb * 2;
        DecimalFormat myformat = new DecimalFormat("######0.0");
        return myformat.format(kg);
    }

    public static String kgToSt(double kg) {
        double lb = kg * 10 * 22046 / 1000;
        int st = (int) (lb / 14);
        if (st % 2 != 0) {
            st++;
        }
        return String.valueOf(keep2Point((float) st / 100));
    }


    public static float keep1Point3(double kg) {
        BigDecimal b = new BigDecimal(kg);
        float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return f1;
    }

    public static float keep2Point(double kg) {
        BigDecimal b = new BigDecimal(kg);
        float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return f1;
    }

    /**
     * 传入kg，根据重量单位得出相应值
     * 体脂秤一直是kg
     * 食物秤需要采用秤返回的单位和重量
     *
     * @param htWeightKg
     * @return
     */
    public static String getWeight(PPUnitType unit, double htWeightKg) {
        if (unit == PPUnitType.Unit_KG) {
            return keep1Point3(htWeightKg) + "kg";
        } else if (unit == PPUnitType.Unit_LB) {
            return kgToLB_ForFatScale(htWeightKg) + "lb";
        } else if (unit == PPUnitType.PPUnitST) {
            return htWeightKg + "st";
        } else if (unit == PPUnitType.PPUnitJin) {
            return kgToJin(htWeightKg) + "斤";
        } else if (unit == PPUnitType.PPUnitG) {
            return htWeightKg + "g";
        } else if (unit == PPUnitType.PPUnitLBOZ) {
            return htWeightKg + "lb:oz";
        } else if (unit == PPUnitType.PPUnitOZ) {
            return htWeightKg + "oz";
        } else if (unit == PPUnitType.PPUnitMLWater) {
            return htWeightKg + "water";
        } else if (unit == PPUnitType.PPUnitMLMilk) {
            return htWeightKg + "milk";
        } else {
            return htWeightKg + "kg";
        }
    }


}
