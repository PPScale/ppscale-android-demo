package com.example.pengsiyuan.ppbluetoothle.util;

import com.peng.ppscale.business.device.PPUnitType;

import java.math.BigDecimal;
import java.text.DecimalFormat;

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
        double kg =  lb * 2;
        DecimalFormat myformat = new DecimalFormat("######0.0");
        return myformat.format(kg);
    }

    public static float keep1Point3(double kg) {
        BigDecimal b = new BigDecimal(kg);
        float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return f1;
    }


    /**
     * 传入kg，根据重量单位得出相应值
     *
     * @param htWeightKg
     * @return
     */
    public static String getWeight(PPUnitType unit, double htWeightKg) {
        if (unit ==PPUnitType.Unit_KG) {
            return keep1Point3(htWeightKg) + "kg";
        } else if (unit == PPUnitType.Unit_LB) {
            return kgToLB_ForFatScale(htWeightKg) + "lb";
        } else {
            return kgToJin(htWeightKg) + "斤";
        }
    }



}
