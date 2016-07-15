package com.freddieptf.shush.calendar.services;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.Utils.AlarmUtils;
import com.freddieptf.shush.calendar.model.EventModel;
import com.freddieptf.shush.calendar.model.SettingsModel;

/**
 * Created by freddieptf on 11/07/16.
 */
public class ShushForegroundService extends Service {

    public final static String ACTION_STOP = "aye_stop";
    public final static String ACTION_SHUSH = "aye_shush";

    final int NOTIFICATION_ID = 2334;
    EventModel event;
    SettingsModel initialSettings;
    NotificationManager notificationManager;
    AudioManager audioManager;
    WifiManager wifiManager;
    AlarmUtils alarmUtils;
    private static final String TAG = "ShushForegroundService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getBaseContext().getSystemService(AUDIO_SERVICE);
        wifiManager = (WifiManager) getBaseContext().getSystemService(WIFI_SERVICE);
        getInitialSettings();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand: " + intent.getAction());

        switch (intent.getAction()){
            case ACTION_STOP:
                stopShush();
                break;

            case ACTION_SHUSH:
                // if var alarmutils is not null, then the service must be already running.
                // we cancel the set stopAlarm so we can update it to the new events stop time
                if(alarmUtils != null) alarmUtils.cancelAlarm();

                event = intent.getParcelableExtra(AlarmUtils.EVENT_EXTRA);
                alarmUtils = new AlarmUtils(this, event);
                alarmUtils.setStopAlarm();
                shush(event);

                startInForeground(); //starting in foreground here so we can get the event name

                break;

            default:

        }

        return START_NOT_STICKY;
    }

    private void startInForeground(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        PendingIntent stop = PendingIntent.getService(this, NOTIFICATION_ID,
                new Intent(this, ShushForegroundService.class).setAction(ACTION_STOP), PendingIntent.FLAG_ONE_SHOT);

        builder.setSmallIcon(R.drawable.ic_action_calendar_month)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Keeping it silence: " + event.getTitle())
                .addAction(R.drawable.ic_clear_black_24dp, "Stop", stop);

        startForeground(NOTIFICATION_ID, builder.build());
    }

    public void stopShush(){
        stopForeground(true);
        removeShushMode();
        this.stopService(new Intent(this, ShushForegroundService.class));
    }

    public void shush(EventModel event){
        setShushMode(event.getSettings());
    }

    @TargetApi(23)
    public boolean canWrite(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return Settings.System.canWrite(getBaseContext());
        else return true;
    }

    private void setShushMode(SettingsModel settingsModel){
        if(settingsModel != null) {
            if (settingsModel.getRingerMode() != -1) audioManager.setRingerMode(settingsModel.getRingerMode());
            if (settingsModel.isWifiDisabled()) wifiManager.setWifiEnabled(false);
            if(settingsModel.getBrightness() != 0 && canWrite())
                setScreenBrightness(settingsModel.getBrightness() / 100 * 255);
        }
    }

    private void getInitialSettings(){
        initialSettings = new SettingsModel();
        initialSettings.setRingerMode(audioManager.getRingerMode());
        initialSettings.setWifiDisabled(!wifiManager.isWifiEnabled());
        try {
            initialSettings.setBrightness(Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS));
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void removeShushMode(){
        audioManager.setRingerMode(initialSettings.getRingerMode());
        wifiManager.setWifiEnabled(initialSettings.isWifiDisabled());
        setScreenBrightness(initialSettings.getBrightness());
    }

    private void setScreenBrightness(int brightness){
        ContentResolver contentResolver = getContentResolver();
        Settings.System.putInt(contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

}
