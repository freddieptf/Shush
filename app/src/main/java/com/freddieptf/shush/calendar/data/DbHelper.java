package com.freddieptf.shush.calendar.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.freddieptf.shush.calendar.model.EventModel;

/**
 * Created by fred on 12/8/15.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "Shush";
    public static final int DB_VERSION = 1;

    public static final String CALENDAR_EVENTS_TABLE = "Calendar_Events";
    public static final String SHUSH_PROFILES_TABLE = "Shush_Profiles";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EVENT_NAME = "event_name";
    public static final String COLUMN_SHUSHPROFILE_NAME = "profile_name";
    public static final String COLUMN_BEGIN = "begins";
    public static final String COLUMN_END = "ends";
    public static final String COLUMN_SILENT_MODE = "sound_mode";
    public static final String COLUMN_WIFI_SSID = "wifi_ssid";
    public static final String COLUMN_SET_BRIGHTNESS = "brightness";
    public static final String COLUMN_WIFI_ENABLED = "wifi_enabled";
    public static final String COLUMN_DATA_ENABLED = "data_enabled";
    public static final String COLUMN_IS_SET = "is_set";
    public static final String COLUMN_REPEATS = "repeat_mode";
    public static final String COLUMN_TRIGGER = "trigger";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CALENDAR_EVENTS_TABLE = "CREATE TABLE " + CALENDAR_EVENTS_TABLE + "(" +
                COLUMN_ID + " INT NOT NULL, " +
                COLUMN_EVENT_NAME + " TEXT NOT NULL, " +
                COLUMN_BEGIN + " LONG NOT NULL, " +
                COLUMN_END + " LONG NOT NULL, " +
                COLUMN_SILENT_MODE + " INT, " +
                COLUMN_SET_BRIGHTNESS + " INT, " +
                COLUMN_WIFI_ENABLED + " INT, " +
                COLUMN_DATA_ENABLED + " INT, " +
                COLUMN_IS_SET + " BOOLEAN, " +
                " UNIQUE (" + COLUMN_ID + ") ON CONFLICT REPLACE " + ");";

        String CREATE_SHUSH_PROFILE_TABLE = "CREATE TABLE " + SHUSH_PROFILES_TABLE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_SHUSHPROFILE_NAME + " TEXT NOT NULL, " +
                COLUMN_SILENT_MODE + " INT, " +
                COLUMN_SET_BRIGHTNESS + " INT, " +
                COLUMN_WIFI_ENABLED + " BOOLEAN, " +
                COLUMN_DATA_ENABLED + " BOOLEAN " + ");";

        sqLiteDatabase.execSQL(CREATE_CALENDAR_EVENTS_TABLE);
        sqLiteDatabase.execSQL(CREATE_SHUSH_PROFILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public boolean eventExists(String id){
        Cursor c = this.query(null, DbHelper.COLUMN_ID + " = ? ",
                new String[]{id}, null);
        if(c != null) return c.moveToFirst();
        else return false;
    }

    public int deleteRowById(String tableName, String id){
        return this.getWritableDatabase().delete(tableName, DbHelper.COLUMN_ID + " = ? ",
                new String[]{id});
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        return this.getReadableDatabase().query(
                DbHelper.CALENDAR_EVENTS_TABLE,
                projection,
                selection,
                selectionArgs,
                null, null,
                sortOrder);
    }

    public long insertEvent(String tableName, EventModel eventModel, String trigger){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.COLUMN_BEGIN, eventModel.getStart());
        contentValues.put(DbHelper.COLUMN_END, eventModel.getEnd());
        contentValues.put(DbHelper.COLUMN_EVENT_NAME, eventModel.getTitle());
        contentValues.put(DbHelper.COLUMN_SILENT_MODE, eventModel.getSettings().getRingerMode());
        contentValues.put(DbHelper.COLUMN_SET_BRIGHTNESS, eventModel.getSettings().getBrightness());
        contentValues.put(COLUMN_DATA_ENABLED, eventModel.getSettings().isMobileDataDisabled() ? 1 : 0);
        contentValues.put(COLUMN_WIFI_ENABLED, eventModel.getSettings().isWifiDisabled() ? 1 : 0);
        if(trigger != null && !trigger.isEmpty()){
            contentValues.put(COLUMN_TRIGGER, trigger);
            if(trigger.equals("wifi")) contentValues.put(DbHelper.COLUMN_WIFI_SSID, eventModel.getWifiSSID());
        }
        long l = db.insert(tableName, null, contentValues);
        if(db.isOpen()) db.close();
        return l;
    }

}
