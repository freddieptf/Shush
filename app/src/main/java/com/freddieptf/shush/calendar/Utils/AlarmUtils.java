package com.freddieptf.shush.calendar.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.freddieptf.shush.calendar.model.EventModel;
import com.freddieptf.shush.calendar.model.SettingsModel;
import com.freddieptf.shush.calendar.receivers.ShushAlarmReceiver;
import com.freddieptf.shush.calendar.services.ShushForegroundService;

/**
 * Created by fred on 12/18/15.
 */
public class AlarmUtils {

    public static final String EVENT_EXTRA = "event_extra";
    public static final String START = "start";
    public static final String SETTINGS_MODEL = "smodel";
    Context context;
    AlarmManager alarmManager;
    EventModel event;

    public AlarmUtils(Context context, EventModel event){
        this.context = context;
        this.event = event;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setSettingsModel(SettingsModel settingsModel){
        event.setSettings(settingsModel);
    }

    private Intent buildAlarmIntent(boolean isStart){
        Intent intent = new Intent(context, ShushAlarmReceiver.class);
        intent.putExtra(EVENT_EXTRA, event)
                .putExtra(START, isStart)
                .setAction(isStart ? ShushForegroundService.ACTION_SHUSH : ShushForegroundService.ACTION_STOP);
        return intent;
    }

    private PendingIntent buildAlarmPendingIntent(boolean isStart){
        return PendingIntent.getBroadcast(context,
                Integer.parseInt(event.getId()), buildAlarmIntent(isStart), PendingIntent.FLAG_ONE_SHOT);
    }

    public void setAlarm(){
        long time = Long.parseLong(event.getStart());
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, buildAlarmPendingIntent(true));
    }

    public void setStopAlarm(){
        long time = Long.parseLong(event.getEnd());
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, buildAlarmPendingIntent(false));
    }

    public void cancelAlarm(){
        alarmManager.cancel(buildAlarmPendingIntent(false));
    }

}
