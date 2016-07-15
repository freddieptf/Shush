package com.freddieptf.shush.calendar.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.freddieptf.shush.calendar.data.DbHelper;
import com.freddieptf.shush.calendar.model.EventModel;
import com.freddieptf.shush.calendar.model.SettingsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fred on 12/9/15.
 */
public class EventsLoader extends AsyncTaskLoader<List<EventModel>> {

    String tableName;
    private static final String TAG = "EventsLoader";


    public EventsLoader(Context context, String tableName) {
        super(context);
        this.tableName = tableName;
        Log.d(TAG, "EventsLoader: " + tableName);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<EventModel> loadInBackground() {
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        List<EventModel> list = new ArrayList<>();

        String[] projection;

        if(tableName.equals(DbHelper.CALENDAR_EVENTS_TABLE)){
            projection = new String[] {
                    DbHelper.COLUMN_ID, //0
                    DbHelper.COLUMN_EVENT_NAME, //1
                    DbHelper.COLUMN_BEGIN, //2
                    DbHelper.COLUMN_END, //3
                    DbHelper.COLUMN_SILENT_MODE, //4
                    DbHelper.COLUMN_SET_BRIGHTNESS, //5
                    DbHelper.COLUMN_WIFI_ENABLED, //6
                    DbHelper.COLUMN_DATA_ENABLED, //7
            };
        }else {
            projection = new String[] {
                    DbHelper.COLUMN_ID, //0
                    DbHelper.COLUMN_EVENT_NAME, //1
                    DbHelper.COLUMN_BEGIN, //2
                    DbHelper.COLUMN_END, //3
                    DbHelper.COLUMN_SILENT_MODE, //4
                    DbHelper.COLUMN_SET_BRIGHTNESS, //5
                    DbHelper.COLUMN_WIFI_ENABLED, //6
                    DbHelper.COLUMN_DATA_ENABLED, //7
                    DbHelper.COLUMN_TRIGGER, //8
                    DbHelper.COLUMN_WIFI_SSID //9
            };
        }

        Cursor cursor = sqLiteDatabase
                .query(tableName, projection, null, null, null, null, DbHelper.COLUMN_BEGIN + " ASC ");

        if(cursor != null && cursor.moveToFirst()){
            do {
                EventModel eventObj = new EventModel();
                eventObj.setId(cursor.getString(0))
                        .setTitle(cursor.getString(1))
                        .setStart(cursor.getString(2))
                        .setEnd(cursor.getString(3))
                        .setSettings(readSettingsFromDb(cursor));
                if(!tableName.equals(DbHelper.CALENDAR_EVENTS_TABLE)){
                    eventObj.setTrigger(cursor.getString(8))
                            .setWifiSSID(cursor.getString(9));
                }
                list.add(eventObj);
            }while (cursor.moveToNext());
            cursor.close();
            sqLiteDatabase.close();
        }

        return list;
    }

    public SettingsModel readSettingsFromDb(Cursor cursor){
        SettingsModel settings = new SettingsModel();
            settings.setRingerMode(cursor.getString(4) == null ? -1 : Integer.parseInt(cursor.getString(4)))
                    .setBrightness( cursor.getString(5) == null ? 0 : Integer.parseInt(cursor.getString(5)))
                    .setWifiDisabled(cursor.getString(6) != null && Integer.parseInt(cursor.getString(6)) == 1)
                    .setMobileDataDisabled(cursor.getString(7) != null && Integer.parseInt(cursor.getString(7)) == 1);
        return settings;
    }
}
