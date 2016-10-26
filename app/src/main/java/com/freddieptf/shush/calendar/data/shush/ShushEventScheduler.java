package com.freddieptf.shush.calendar.data.shush;

import android.util.Log;

import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.freddieptf.shush.calendar.data.model.Event;

/**
 * Created by freddieptf on 21/10/16.
 */

public class ShushEventScheduler {

    private static final String TAG = "ShushEventScheduler";
    public static String ID = "evidk";
    public static String START = "start";
    public static String END = "end";
    public static String NAME = "name";
    public static String RINGER_MODE = "shush_mode";
    public static String BRIGHTNESS = "brightness";
    public static String WIFI_DISABLED = "wifi_offf";
    public static String END_JOB = "end_jobb";

    public static void cancelScheduled(Event event){

    }

    public static void schedule(Event event){
        PersistableBundleCompat bundleCompat = new PersistableBundleCompat();
        Log.d(TAG, "schedule: " + event.getName() + "/" + event.getId());
        bundleCompat.putLong(ID, event.getId());
        bundleCompat.putLong(END, event.getEndTime());
        bundleCompat.putLong(START, event.getStartTime());
        bundleCompat.putString(NAME, event.getName());
        bundleCompat.putInt(RINGER_MODE, event.getShushProfile().getRingerMode());
        bundleCompat.putInt(BRIGHTNESS, event.getShushProfile().getBrightness());
        bundleCompat.putBoolean(WIFI_DISABLED, event.getShushProfile().isWifiDisabled());

        long offset = event.getStartTime() - System.currentTimeMillis();

        new JobRequest.Builder(ShushJob.TAG)
                .setExact(offset)
                .setExtras(bundleCompat)
                .setPersisted(true)
                .build()
                .schedule();
    }

}
