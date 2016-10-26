package com.freddieptf.shush.calendar.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.freddieptf.shush.calendar.data.model.Event;
import com.freddieptf.shush.calendar.data.model.ShushProfile;

import static com.freddieptf.shush.calendar.data.Contract.CalendarEventsTable;
import static com.freddieptf.shush.calendar.data.Contract.ShushProfileTable;

/**
 * Created by fred on 12/8/15.
 */
class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Shush";
    private static final int DB_VERSION = 3;

    DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CALENDAR_EVENTS_TABLE = "CREATE TABLE " + CalendarEventsTable.TABLE_NAME+ "(" +
                CalendarEventsTable._ID + " INTEGER NOT NULL, " +
                CalendarEventsTable.COLUMN_EVENT_NAME + " TEXT NOT NULL, " +
                CalendarEventsTable.COLUMN_BEGIN + " LONG NOT NULL, " +
                CalendarEventsTable.COLUMN_END + " LONG NOT NULL, " +
                CalendarEventsTable.COLUMN_IS_SET + " BOOLEAN, " +
                " UNIQUE (" + CalendarEventsTable._ID + ") ON CONFLICT REPLACE " + ");";

        String CREATE_SHUSH_PROFILE_TABLE = "CREATE TABLE " + ShushProfileTable.TABLE_NAME + " (" +
                ShushProfileTable._ID + " INTEGER PRIMARY KEY, " +
                ShushProfileTable.COLUMN_EVENT_KEY + " INTEGER NOT NULL, " +
                ShushProfileTable.COLUMN_PROFILE_NAME + " TEXT, " +
                ShushProfileTable.COLUMN_SILENT_MODE + " INTEGER, " +
                ShushProfileTable.COLUMN_BRIGHTNESS + " INTEGER, " +
                ShushProfileTable.COLUMN_WIFI_DISABLED + " BOOLEAN, " +
                ShushProfileTable.COLUMN_DATA_DISABLED + " BOOLEAN, " +
                "FOREIGN KEY (" + ShushProfileTable.COLUMN_EVENT_KEY + ") REFERENCES "
                + CalendarEventsTable.TABLE_NAME + "(" + CalendarEventsTable._ID + ")"
                +");";

        sqLiteDatabase.execSQL(CREATE_CALENDAR_EVENTS_TABLE);
        sqLiteDatabase.execSQL(CREATE_SHUSH_PROFILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CalendarEventsTable.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ShushProfileTable.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    boolean eventExists(String id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(CalendarEventsTable.TABLE_NAME, null, CalendarEventsTable._ID + " = ? ",
                new String[]{id}, null, null, null);
        boolean exists = false;
        if(c != null){
             exists = c.moveToFirst();
            if(!c.isClosed()) c.close();
        }
        if(db.isOpen()) db.close();
        return exists;
    }

    ShushProfile getShushProfileIfExists(long eventId){
        String[] projection = {
                ShushProfileTable.COLUMN_SILENT_MODE,
                ShushProfileTable.COLUMN_BRIGHTNESS,
                ShushProfileTable.COLUMN_WIFI_DISABLED,
                ShushProfileTable.COLUMN_DATA_DISABLED,
        };

        ShushProfile shushProfile = null;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                ShushProfileTable.TABLE_NAME,
                projection,
                ShushProfileTable.COLUMN_EVENT_KEY + " = ?", new String[]{eventId + ""},
                null, null, null);

        if(cursor != null && cursor.moveToFirst()){
            ShushProfile.Builder builder = new ShushProfile.Builder();
            builder.setRingerMode(cursor.getInt(0))
                    .setBrightness(cursor.getInt(1))
                    .setWifiDisabled(cursor.getInt(2) != 0)
                    .setMobileDataDisbaled(cursor.getInt(3) != 0);
            shushProfile = builder.build();

            if(!cursor.isClosed()) cursor.close();
        }
        return shushProfile;
    }

    int deleteEvent(String id){
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query(ShushProfileTable.TABLE_NAME,
                null, ShushProfileTable.COLUMN_EVENT_KEY + " = ?", new String[]{id + ""}, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
            db.delete(ShushProfileTable.TABLE_NAME,
                    ShushProfileTable.COLUMN_EVENT_KEY + " = ?",
                    new String[]{id + ""});
            if(!cursor.isClosed()) cursor.close();
        }

        int rows = db.delete(CalendarEventsTable.TABLE_NAME, CalendarEventsTable._ID + " = ? ",
                new String[]{id});

        if(db.isOpen()) db.close();
        return rows;
    }

    Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                CalendarEventsTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null, null,
                sortOrder);
        return cursor;
    }

    long insertEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarEventsTable._ID, event.getId());
        contentValues.put(CalendarEventsTable.COLUMN_EVENT_NAME, event.getName());
        contentValues.put(CalendarEventsTable.COLUMN_BEGIN, event.getStartTime());
        contentValues.put(CalendarEventsTable.COLUMN_END, event.getEndTime());
        if(event.getShushProfile() == null){
            contentValues.put(CalendarEventsTable.COLUMN_IS_SET, false);
        }
        long l = db.insert(CalendarEventsTable.TABLE_NAME, null, contentValues);
        if(db.isOpen()) db.close();
        return l;
    }

    long insertShushProfile(long eventId, ShushProfile shushProfile){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ShushProfileTable.COLUMN_EVENT_KEY, eventId);
        contentValues.put(ShushProfileTable.COLUMN_BRIGHTNESS, shushProfile.getBrightness());
        contentValues.put(ShushProfileTable.COLUMN_WIFI_DISABLED, shushProfile.isWifiDisabled());
        contentValues.put(ShushProfileTable.COLUMN_SILENT_MODE, shushProfile.getRingerMode());
        contentValues.put(ShushProfileTable.COLUMN_DATA_DISABLED, shushProfile.isMobileDataDisbaled());

        Cursor cursor = db.query(ShushProfileTable.TABLE_NAME,
                new String[]{ShushProfileTable.COLUMN_EVENT_KEY},
                ShushProfileTable.COLUMN_EVENT_KEY + " = ?",
                new String[]{eventId + ""},
                null, null, null);

        long id;
        if(cursor != null && cursor.moveToFirst()){
            id = db.update(ShushProfileTable.TABLE_NAME, contentValues,
                    ShushProfileTable.COLUMN_EVENT_KEY + " = ?", new String[]{eventId + ""});
            if(!cursor.isClosed()) cursor.close();
        }else {
            id = db.insert(ShushProfileTable.TABLE_NAME, null, contentValues);
        }

        if(id > -1){
            contentValues = new ContentValues();
            contentValues.put(CalendarEventsTable.COLUMN_IS_SET, true);
            db.update(CalendarEventsTable.TABLE_NAME, contentValues,
                    CalendarEventsTable._ID + " = ?", new String[]{eventId + ""});
        }

        if(db.isOpen()) db.close();
        return id;
    }

    int updateEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarEventsTable.COLUMN_EVENT_NAME, event.getName());
        contentValues.put(CalendarEventsTable.COLUMN_BEGIN, event.getStartTime());
        contentValues.put(CalendarEventsTable.COLUMN_END, event.getEndTime());

        int update = db.update(CalendarEventsTable.TABLE_NAME, contentValues,
                CalendarEventsTable._ID + " =? ", new String[]{event.getId() + ""});

        if(db.isOpen()) db.close();
        return update;
    }

}
