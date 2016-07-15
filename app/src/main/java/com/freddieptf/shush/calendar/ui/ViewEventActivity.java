package com.freddieptf.shush.calendar.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freddieptf.shush.BuildConfig;
import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.Utils.SettingsChangeHelper;
import com.freddieptf.shush.calendar.model.EventModel;
import com.freddieptf.shush.calendar.model.SettingsModel;
import com.freddieptf.shush.calendar.ui.views.EditEventDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by freddieptf on 2/15/16.
 */
public class ViewEventActivity extends AppCompatActivity implements SettingsChangeHelper {

    @Bind(R.id.textDate) TextView tvDate;
    @Bind(R.id.textTime) TextView tvTime;
    @Bind(R.id.event_title) TextView tvEventTitle;
    @Bind(R.id.tvSoundSetting) TextView tvSound;
    @Bind(R.id.tvBrightnessSetting) TextView tvBrightness;
    @Bind(R.id.tvWifiSetting) TextView tvWifi;
    @Bind(R.id.lvSoundStatus) LinearLayout soundStatus;
    @Bind(R.id.lvBrightnessStatus) LinearLayout brightnessStatus;
    @Bind(R.id.lvWifiStatus) LinearLayout wifiStatus;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private static final String TAG = "ViewEventActivity";
    public static String EVENT_KEY = "event_keyy";
    public static String SETTINGS_KEY = "settings_keyy";

    EventModel event;
    SettingsModel settingsModel;

    @OnClick(R.id.fab_edit) void editSettings(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) showEditEventDialog();
            else requestWriteSettings();
        }else {
            showEditEventDialog();
        }
    }

    private void showEditEventDialog() {
        EditEventDialog editEventDialog = new EditEventDialog();
        Bundle b = new Bundle();
        b.putParcelable(EVENT_KEY, event);
        b.putString(MainActivity.TABlE_NAME_KEY, getIntent().getStringExtra(MainActivity.TABlE_NAME_KEY));
        editEventDialog.setArguments(b);
        editEventDialog.show(getSupportFragmentManager(), "ed");
        editEventDialog.setStyle(DialogFragment.STYLE_NO_TITLE,
                R.style.AppTheme);
    }

    @TargetApi(23)
    private void requestWriteSettings() {
        if (!android.provider.Settings.System.canWrite(this)) {
            Intent i = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
            startActivityForResult(i, 34, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 34 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(Settings.System.canWrite(this)){
                showEditEventDialog();
            }else{

            }
        }
    }

    @Override
    public void onSettingsChange(SettingsModel settingsModel) {
        this.settingsModel = settingsModel;
        setStatusViewUp();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        ButterKnife.bind(this);

//        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        event = getIntent().getParcelableExtra(EVENT_KEY);
        settingsModel = event.getSettings();
        tvEventTitle.setText(event.getTitle());
        initEventIndicator();
        setStatusViewUp();
    }

    private void initEventIndicator(){
        Date startDate = new Date(Long.parseLong(event.getStart()));
        Date endDate = new Date(Long.parseLong(event.getEnd()));

        DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.FULL);
        if (!df.format(startDate).equals(df.format(endDate))) {
            df = SimpleDateFormat.getDateInstance(DateFormat.LONG);
            tvDate.setText(df.format(startDate) + " - " + df.format(endDate));
        } else tvDate.setText(df.format(startDate));

        df = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
        tvTime.setText(df.format(startDate) + " - " + df.format(endDate));

//        if(event.getTrigger() != null && event.getTrigger().equals("wifi")){
//            tvTime.setText("Wifi SSID: " + event.getWifiSSID());
//        }

    }

    private void setStatusViewUp(){
        if(settingsModel.getRingerMode() != -1){
            tvSound.setText(settingsModel.getRingerMode() == AudioManager.RINGER_MODE_SILENT ? "Silent" : "Vibrate");
            soundStatus.setVisibility(View.VISIBLE);
        } else soundStatus.setVisibility(View.GONE);

        if(settingsModel.getBrightness() != 0){
            tvBrightness.setText(settingsModel.getBrightness() + "%");
            brightnessStatus.setVisibility(View.VISIBLE);
        } else brightnessStatus.setVisibility(View.GONE);

        if(settingsModel.isWifiDisabled()){
            tvWifi.setText("Disabled");
            wifiStatus.setVisibility(View.VISIBLE);
        } else wifiStatus.setVisibility(View.GONE);
    }

}
