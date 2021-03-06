package com.lefu.ppscale.wifi.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.lefu.ppscale.wifi.model.DeviceModel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DEVICE_MODEL".
*/
public class DeviceModelDao extends AbstractDao<DeviceModel, Long> {

    public static final String TABLENAME = "DEVICE_MODEL";

    /**
     * Properties of entity DeviceModel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DeviceMac = new Property(1, String.class, "deviceMac", false, "DEVICE_MAC");
        public final static Property DeviceName = new Property(2, String.class, "deviceName", false, "DEVICE_NAME");
        public final static Property DeviceType = new Property(3, int.class, "deviceType", false, "DEVICE_TYPE");
        public final static Property Sn = new Property(4, String.class, "sn", false, "SN");
        public final static Property Ssid = new Property(5, String.class, "ssid", false, "SSID");
    }


    public DeviceModelDao(DaoConfig config) {
        super(config);
    }
    
    public DeviceModelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DEVICE_MODEL\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"DEVICE_MAC\" TEXT UNIQUE ," + // 1: deviceMac
                "\"DEVICE_NAME\" TEXT," + // 2: deviceName
                "\"DEVICE_TYPE\" INTEGER NOT NULL ," + // 3: deviceType
                "\"SN\" TEXT," + // 4: sn
                "\"SSID\" TEXT);"); // 5: ssid
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DEVICE_MODEL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DeviceModel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String deviceMac = entity.getDeviceMac();
        if (deviceMac != null) {
            stmt.bindString(2, deviceMac);
        }
 
        String deviceName = entity.getDeviceName();
        if (deviceName != null) {
            stmt.bindString(3, deviceName);
        }
        stmt.bindLong(4, entity.getDeviceType());
 
        String sn = entity.getSn();
        if (sn != null) {
            stmt.bindString(5, sn);
        }
 
        String ssid = entity.getSsid();
        if (ssid != null) {
            stmt.bindString(6, ssid);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DeviceModel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String deviceMac = entity.getDeviceMac();
        if (deviceMac != null) {
            stmt.bindString(2, deviceMac);
        }
 
        String deviceName = entity.getDeviceName();
        if (deviceName != null) {
            stmt.bindString(3, deviceName);
        }
        stmt.bindLong(4, entity.getDeviceType());
 
        String sn = entity.getSn();
        if (sn != null) {
            stmt.bindString(5, sn);
        }
 
        String ssid = entity.getSsid();
        if (ssid != null) {
            stmt.bindString(6, ssid);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public DeviceModel readEntity(Cursor cursor, int offset) {
        DeviceModel entity = new DeviceModel( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // deviceMac
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // deviceName
            cursor.getInt(offset + 3), // deviceType
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // sn
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // ssid
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DeviceModel entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDeviceMac(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDeviceName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDeviceType(cursor.getInt(offset + 3));
        entity.setSn(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setSsid(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(DeviceModel entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(DeviceModel entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DeviceModel entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
