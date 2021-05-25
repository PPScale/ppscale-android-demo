package com.lefu.ppscale.wifi;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;


import com.lefu.ppscale.wifi.dao.DaoMaster;
import com.lefu.ppscale.wifi.dao.DaoSession;
import com.peng.ppscale.business.ble.PPScale;

public class PPApplication extends Application {
    public static  String tableName = "";
    private static PPApplication mApp;
    private static DaoSession mDaoSession;
    private static SQLiteDatabase sqLiteDatabase;

    private static PPApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        initGreenDao();
        instance = this;

        PPScale.init(this);
        PPScale.setDebug(true);
    }


    private void initGreenDao(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mApp, "pplibrary.db");
        sqLiteDatabase = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(sqLiteDatabase);
        mDaoSession = daoMaster.newSession();
    }

    public static DaoSession getmDaoSession(){
        return  mDaoSession;
    }

    public static SQLiteDatabase getSqLiteDatabase() {
        return sqLiteDatabase;
    }

    public static Application getInstance() {
        return instance;
    }
}

