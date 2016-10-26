package com.freddieptf.shush.calendar.services;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.freddieptf.shush.calendar.data.EventsRepository;

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
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            EventsRepository.getInstance().syncWhenCalendarChange(getApplicationContext());
        }
    }

}
