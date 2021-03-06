package com.lefu.ppscale.ble.model;

import android.text.TextUtils;
import android.util.Log;

import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.util.DeviceUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class PPUtil {

    public static String kgToJin(double lb) {
        double kg = lb * 2;
        DecimalFormat myformat = new DecimalFormat("######0.0");
        return myformat.format(kg);
    }

    /**
     * 不四舍五入
     * <p>
     * 151.25
     * 15125
     *
     * @param tempKg
     * @return
     */
    public static float keep1Point1_(double tempKg) {
        int intValue = (int) (tempKg * 10);
        double value = (double) intValue / 10;
        BigDecimal b = new BigDecimal(value);
        float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return f1;
    }

    /**
     * 四舍五入 两位
     *
     * @param tempKg
     * @return
     */
    public static float keep1Point2(double tempKg) {
        tempKg = (tempKg * 10000 + 5) / 10000;
        BigDecimal b = new BigDecimal(tempKg);
        float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return f1;
    }

    /**
     * 四舍五入 一位
     *
     * @param kg
     * @return
     */
    public static float keep1Point3(double kg) {
        BigDecimal b = new BigDecimal(kg);
        float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return f1;
    }

    /**
     * 四舍五入
     *
     * @param tempKg
     * @return
     */
    public static float keep1Point1(double tempKg) {
        tempKg = (tempKg * 100 + 0.5) / 100;
        BigDecimal b = new BigDecimal(tempKg);
        float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
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
    public static String getWeight(PPUnitType unit, double htWeightKg, String scaleName) {
        if (unit == PPUnitType.Unit_KG) {
            return getWeightValue(unit, htWeightKg, scaleName) + "kg";
        } else if (unit == PPUnitType.Unit_LB) {
            return getWeightValue(unit, htWeightKg, scaleName) + "lb";
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

    /**
     * 传入kg，根据重量单位得出相应值 心率称保留两位，其他称保留一位
     *
     * @param htWeightKg
     * @return
     */
    public synchronized static float getWeightValue(PPUnitType unit, double htWeightKg, String scaleName) {
        if (isTwoPointScale(scaleName)) {
            if (unit == PPUnitType.Unit_KG) {
                if (htWeightKg < 100) {
                    return keep1Point2(htWeightKg);
                } else {
                    return keep1Point1_(htWeightKg);
                }
            } else if (unit == PPUnitType.Unit_LB) {//kgToLB_Extra
                return kgToLB_2Point_tofloat(htWeightKg);
            }
        } else {
            if (unit == PPUnitType.Unit_KG) {
                return keep1Point1_(htWeightKg);
            } else if (unit == PPUnitType.Unit_LB) {//kgToLB_Extra
                return kgToLB_Point_tofloat(htWeightKg);
            }
        }
        return keep1Point1(htWeightKg);
    }

    private static boolean isTwoPointScale(String scaleType) {
        return !TextUtils.isEmpty(scaleType) && (DeviceUtil.Point2_Scale_List.contains(scaleType));
    }

    /**
     * 分度值为0.1   KG转LB 则LB 为无奇数显示
     *
     * @param tempKg
     * @return
     */
    public static float kgToLB_Point_tofloat(double tempKg) {
        // 参照ios
        if (0 == tempKg) return 0.0f;
//        int temp = (int) ((tempKg * 100 + 5) / 10);
//        float tempFloat =  temp / 10.0f;
        int temp2_2046 = (int) Math.floor((tempKg * 10 * 22046 / 10000));
        if (temp2_2046 % 2 > 0) {
            temp2_2046++;
        }
        return keep1Point1(temp2_2046 / 10.0f);
//        BigDecimal bigDecimal = new BigDecimal(temp2_2046 / 10.0f);
//        return bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }


    /**
     * 分度值为0.05 KG转LB
     *
     * @param tempKg
     * @return
     */
    public static float kgToLB_2Point_tofloat(double tempKg) {
        Log.d("kgToLB_2Point_tofloat", "" + tempKg);
        if (0 == tempKg) return 0.0f;
        int temp = (int) Math.round(tempKg * 10);
        float value = temp / 10.f;  //63.7
        int tempLb = (int) Math.floor(value * 10 * 22046 / 10000);
        return keep1Point1(tempLb / 10.0f);
    }


}
