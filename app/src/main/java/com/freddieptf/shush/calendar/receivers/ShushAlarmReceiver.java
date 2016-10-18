package com.freddieptf.shush.calendar.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.freddieptf.shush.calendar.Utils.AlarmUtils;
import com.freddieptf.shush.calendar.model.EventModel;
import com.freddieptf.shush.calendar.services.ShushForegroundService;

/**
 * Created by fred on 12/18/15.
 */
public class ShushAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "ShushAlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        Intent i = new Intent(context, ShushForegroundService.class);
        EventModel eventModel = intent.getParcelableExtra(AlarmUtils.EVENT_EXTRA);
        i.putExtra(AlarmUtils.EVENT_EXTRA, eventModel)
                .putExtra(AlarmUtils.START, intent.getBooleanExtra(AlarmUtils.START, false))
                .setAction(intent.getAction());
        context.startService(i);
    }
}
