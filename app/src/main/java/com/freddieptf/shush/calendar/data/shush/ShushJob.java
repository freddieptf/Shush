package com.freddieptf.shush.calendar.data.shush;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.Utils.PrefUtils;
import com.freddieptf.shush.calendar.data.model.ShushProfile;

/**
 * Created by freddieptf on 21/10/16.
 */

public class ShushJob extends Job {

    public static final String TAG = "ShushJob";
    private AudioManager audioManager;
    private WifiManager wifiManager;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {

        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        PersistableBundleCompat b = params.getExtras();
        long event_id = b.getLong(ShushEventScheduler.ID, -1);
        String event_name = b.getString(ShushEventScheduler.NAME, "No name");

        Log.d(TAG, "onRunJob: " + event_name + "/" + event_id);

        if(!b.containsKey(ShushEventScheduler.END_JOB)) {
            b.putInt(ShushEventScheduler.END_JOB, 1);

            if(!PrefUtils.isJobRunning(getContext())) {
                saveInitialSettings(audioManager.getRingerMode(), readScreenBrightness(), !wifiManager.isWifiEnabled());
            }

            PrefUtils.updateRunningJobId(getContext(), event_id);
            createStartNotification(event_id, event_name);
            applyShushProfile(
                    b.getInt(ShushEventScheduler.RINGER_MODE, 0),
                    b.getInt(ShushEventScheduler.BRIGHTNESS, 0),
                    b.getBoolean(ShushEventScheduler.WIFI_DISABLED, false));

            long offset = b.getLong(ShushEventScheduler.END, 0) - System.currentTimeMillis();
            new JobRequest.Builder(TAG)
                    .setExact(offset)
                    .setExtras(b)
                    .build().schedule();
        }
        else {
            long runningJob = PrefUtils.getRunningJobId(getContext());
            Log.d(TAG, "onRunJob: stopping: " + runningJob + "//" + event_id);

            if(event_id == runningJob){
                stopShushMode();
                PrefUtils.flushStateEndJob(getContext());
            }

            createEndNotification(event_id, event_name);

        }

        return Result.SUCCESS;
    }

    private void createStartNotification(long id, String name){
        NotificationManager notificationManager = (NotificationManager)
                getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());

        builder.setSmallIcon(R.drawable.ic_action_calendar_month)
                .setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .setContentTitle("Shush: " + name);
        notificationManager.notify((int)id, builder.build());
    }

    private void createEndNotification(long id, String name){
        NotificationManager notificationManager = (NotificationManager)
                getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());

        builder.setSmallIcon(R.drawable.ic_action_calendar_month)
                .setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .setContentTitle("Shush End: " + name);
        notificationManager.notify((int)id, builder.build());
    }

    private void saveInitialSettings(int ringerMode, int brightness, boolean wifiDisabled){
        PrefUtils.writeState(getContext(), ringerMode, brightness, wifiDisabled);
    }

    private void applyShushProfile(int ringerMode, int brightness, boolean wifiDisabled){
        audioManager.setRingerMode(ringerMode);
        setScreenBrightness(brightness);
        wifiManager.setWifiEnabled(!wifiDisabled);
    }

    private void stopShushMode(){
        ShushProfile s = PrefUtils.getState(getContext());
        if(s != null) {
            applyShushProfile(s.getRingerMode(), s.getBrightness(), s.isWifiDisabled());
        }else Log.d(TAG, "stopShushMode: profile null");
    }

    private void setScreenBrightness(int brightness){
        ContentResolver contentResolver = getContext().getContentResolver();
        Settings.System.putInt(contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    private int readScreenBrightness(){
        try {
            return Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
