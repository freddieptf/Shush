package com.freddieptf.shush.calendar.services;

import android.Manifest;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import com.freddieptf.shush.calendar.Log;
import com.freddieptf.shush.calendar.data.DbHelper;
import com.freddieptf.shush.calendar.ui.frags.CalendarEventsFragment;

import java.util.Calendar;

/**
 * Created by fred on 12/18/15.
 */
public class CalendarChangeService extends IntentService {

    private static final String TAG = "CalendarChangeService";

    public CalendarChangeService() {
        super("CalendarChangeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "OnHandleIntent");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI,
                CalendarEventsFragment.EVENTS_PROJECTION, null, null, null);
        int processed = CalendarEventsFragment.processCalendarEvents(getBaseContext(), cursor);
        Log.d(TAG, "processed: " + processed);
        deleteDeletedEvent(contentResolver);
        if (cursor != null && !cursor.isClosed()) cursor.close();
    }


    //lol
    void deleteDeletedEvent(ContentResolver cr) {
        DbHelper dbHelper = new DbHelper(getBaseContext());
        Cursor cursor = dbHelper.query(new String[]{DbHelper.COLUMN_ID}, null, null, null);

        if (cursor.moveToFirst()) {
            Cursor c;
            int deleted = 0;

            Calendar beginTime = Calendar.getInstance();
            long startMillis = beginTime.getTimeInMillis();
            Calendar endTime = Calendar.getInstance();
            endTime.add(Calendar.DATE, 7); //get the events in the next 7 days
            long endMillis = endTime.getTimeInMillis();

            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, startMillis);
            ContentUris.appendId(builder, endMillis);
            Uri uri = builder.build();

            while (cursor.moveToNext()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                c = cr.query(uri,
                        new String[]{CalendarContract.Instances.EVENT_ID},
                        CalendarContract.Instances.EVENT_ID + " = ?", new String[]{cursor.getString(0)}, null);

                if (c != null && c.moveToFirst()){}
                else {
                    Log.d(TAG, "NO: " + cursor.getString(0));
                    deleted += dbHelper.deleteRowById(DbHelper.CALENDAR_EVENTS_TABLE, cursor.getString(0));
                }
            }

            Log.d(TAG, "deleted: " + deleted);
        }

    }


}
