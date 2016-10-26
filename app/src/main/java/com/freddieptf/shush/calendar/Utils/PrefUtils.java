package com.freddieptf.shush.calendar.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.freddieptf.shush.calendar.data.model.ShushProfile;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by fred on 12/9/15.
 */
public class PrefUtils {

    private static final String TAG = "PrefUtils";
    private final static String CalendarSyncPreference = "sync_pref";
    private final static String ShushServiceWritePhoneState = "phone_state";
    private final static String IS_JOB_RUNNING = "job_runing";
    private final static String JOB_ID = "job_id";
    private final static String SyncPrefName = "allowSync";

    private PrefUtils(){
    }

    public static boolean allowSync(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CalendarSyncPreference, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SyncPrefName, false);
    }

    public static void writeSyncPref(Context context, boolean allowSync){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CalendarSyncPreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SyncPrefName, allowSync);
        editor.apply();
    }

    // get and store the state before shush mode is activated
    public static void writeState(Context context, int ringerMode, int brightness, boolean wifiDisbaled){
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(ShushServiceWritePhoneState, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> mode = new HashSet<>();
        mode.add(String.valueOf("0-" + wifiDisbaled));
        mode.add(String.valueOf("1-" + ringerMode));
        mode.add(String.valueOf("2-" + brightness));
        editor.putStringSet(ShushServiceWritePhoneState, mode);
        editor.putBoolean(IS_JOB_RUNNING, true);
        editor.apply();
    }

    public static ShushProfile getState(Context context){
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(ShushServiceWritePhoneState, Context.MODE_PRIVATE);
        Set<String> mode = sharedPreferences.getStringSet(ShushServiceWritePhoneState, null);
        if(mode == null){
            Log.d(TAG, "getState: null");
            return null;
        }
        String[] m = new String[mode.size()];
        mode.toArray(m);
        ShushProfile.Builder builder = new ShushProfile.Builder();
        for (String s : m) {
            String[] ss = s.split("-");
            if(Integer.parseInt(ss[0]) == 0){
                builder.setWifiDisabled(Boolean.parseBoolean(ss[1]));
            }else if(Integer.parseInt(ss[0]) == 1){
                builder.setRingerMode(Integer.parseInt(ss[1]));
            }else if(Integer.parseInt(ss[0]) == 2){
                builder.setBrightness(Integer.parseInt(ss[1]));
            }
        }
        return builder.build();
    }

    public static boolean isJobRunning(Context context){
        return context.getSharedPreferences(ShushServiceWritePhoneState, Context.MODE_PRIVATE)
                .getBoolean(IS_JOB_RUNNING, false);
    }

    public static void updateRunningJobId(Context context, long id){
        SharedPreferences s = context.getSharedPreferences(ShushServiceWritePhoneState, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = s.edit();
        editor.putLong(JOB_ID, id);
        editor.apply();
    }

    public static long getRunningJobId(Context context){
        return context.getSharedPreferences(ShushServiceWritePhoneState, Context.MODE_PRIVATE)
                .getLong(JOB_ID, -1);
    }

    public static void flushStateEndJob(Context context){
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(ShushServiceWritePhoneState, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> mode = Collections.emptySet();
        editor.putStringSet(ShushServiceWritePhoneState, mode);
        editor.putBoolean(IS_JOB_RUNNING, false);
        editor.putLong(JOB_ID, -1);
        editor.apply();
    }



}
