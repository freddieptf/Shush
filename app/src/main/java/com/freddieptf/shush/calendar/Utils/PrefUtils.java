package com.freddieptf.shush.calendar.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;

import java.util.Collections;
import java.util.Set;

/**
 * Created by fred on 12/9/15.
 */
public class PrefUtils {

    private static final String LOG_TAG = "PrefUtils";
    final static String CalendarSyncPreference = "sync_pref";
    final static String ShushServiceWritePhoneState = "phone_state";
    final static String SyncPrefName = "allowSync";
    Context context;
    SharedPreferences sharedPreferences;

    public PrefUtils(Context context){
        this.context = context;
    }

    public boolean allowSync(){
        sharedPreferences = context.getSharedPreferences(CalendarSyncPreference, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SyncPrefName, false);
    }

    public void writeSyncPref(boolean allowSync){
        sharedPreferences = context.getSharedPreferences(CalendarSyncPreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SyncPrefName, allowSync);
        editor.apply();
    }

    // get and store the state before shush service is activated
    public void writeState(){
        sharedPreferences = context.getSharedPreferences(ShushServiceWritePhoneState, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int i = audioManager.getRingerMode();

        Set<String> s = Collections.emptySet();
        s.add(String.valueOf(i));

        editor.putStringSet("state", s);
        editor.apply();
    }



}
