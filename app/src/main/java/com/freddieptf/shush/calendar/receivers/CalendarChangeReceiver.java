package com.freddieptf.shush.calendar.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.freddieptf.shush.calendar.services.CalendarChangeService;

/**
 * Created by fred on 12/18/15.
 */
public class CalendarChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "CalendarChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: " + intent.toUri(Intent.URI_INTENT_SCHEME));
        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_PROVIDER_CHANGED)) {
            context.startService(new Intent(context, CalendarChangeService.class));
        }
    }
}
