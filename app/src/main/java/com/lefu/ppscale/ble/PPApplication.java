package com.lefu.ppscale.ble;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;


import com.lefu.ppscale.ble.dao.DaoMaster;
import com.lefu.ppscale.ble.dao.DaoSession;
import com.peng.ppscale.business.ble.PPScale;

public class PPApplication extends Application {
    public static  String tableName = "";
    private static PPApplication mApp;
    private static DaoSession mDaoSession;
    private static SQLiteDatabase sqLiteDatabase;
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        initGreenDao();

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
}

