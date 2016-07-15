package com.freddieptf.shush.calendar.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.freddieptf.shush.calendar.Log;
import com.freddieptf.shush.calendar.Utils.AlarmUtils;
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
        i.putExtra(AlarmUtils.EVENT_EXTRA, intent.getParcelableExtra(AlarmUtils.EVENT_EXTRA))
                .putExtra(AlarmUtils.START, intent.getBooleanExtra(AlarmUtils.START, false))
                .setAction(intent.getAction());
        context.startService(i);
    }
}
