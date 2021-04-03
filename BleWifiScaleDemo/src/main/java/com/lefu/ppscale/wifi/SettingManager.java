package com.lefu.ppscale.wifi;

import android.content.Context;
import android.content.SharedPreferences;

import com.peng.ppscale.util.Logger;

public class SettingManager {

    private final String SHARE_PREFERENCES = "set";
    private final String UID = "UID";

    private Context context;
    private static SettingManager settingManager;
    private static SharedPreferences spf;

    private SettingManager(Context context) {
        this.context = context;
        spf = context.getSharedPreferences(SHARE_PREFERENCES, Context.MODE_PRIVATE);
    }


    public static SettingManager get() {
        if (settingManager == null) {
            synchronized (SettingManager.class) {
                if (settingManager == null) {
                    settingManager = new SettingManager(PPApplication.getInstance());
                }
            }
        }
        return settingManager;
    }

    public String getUid() {
        return spf.getString(UID, "");
    }

    public void setUid(String uid) {
        Logger.d("uid" + uid);
        spf.edit().putString(UID, uid).commit();
    }


}
