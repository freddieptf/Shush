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
import com.freddieptf.shush.calendar.Utils.EventShushProfileChangeCallback;
import com.freddieptf.shush.calendar.data.model.Event;
import com.freddieptf.shush.calendar.data.model.ShushProfile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by freddieptf on 2/15/16.
 */
public class EventActivity extends AppCompatActivity implements EventShushProfileChangeCallback {

    @Bind(R.id.textDate) TextView tvDate;
    @Bind(R.id.textTime) TextView tvTime;
    @Bind(R.id.event_title) TextView eventName;
    @Bind(R.id.tvSoundSetting) TextView tvSound;
    @Bind(R.id.tvBrightnessSetting) TextView tvBrightness;
    @Bind(R.id.tvWifiSetting) TextView tvWifi;
    @Bind(R.id.lvSoundStatus) LinearLayout soundStatus;
    @Bind(R.id.lvBrightnessStatus) LinearLayout brightnessStatus;
    @Bind(R.id.lvWifiStatus) LinearLayout wifiStatus;
    @Bind(R.id.toolbar) Toolbar toolbar;

    private static final String TAG = "ViewEventActivity";
    public static String EVENT_KEY = "event_keyy";
    private Event event;

    @OnClick(R.id.fab_edit) void editSettings(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) showEditEventDialog();
            else requestWriteSettings();
        }else {
            showEditEventDialog();
        }
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
            }else{}
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        event = getIntent().getParcelableExtra(EVENT_KEY);
        initEventIndicator();
        eventName.setText(event.getName());
        setStatusViewUp();
    }

    @Override
    public void onProfileChange(Event event) {
        this.event = event;
        setStatusViewUp();
    }

    private void showEditEventDialog() {
        EventEditor eventEditor = new EventEditor();
        EventEditorPresenter presenter = new EventEditorPresenter(event, eventEditor);
        eventEditor.show(getSupportFragmentManager(), "edit_event_dialog");
        eventEditor.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
    }

    private void initEventIndicator() {
        Date startDate = new Date(event.getStartTime());
        Date endDate = new Date(event.getEndTime());
        DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.FULL);
        if (!df.format(startDate).equals(df.format(endDate))) {
            df = SimpleDateFormat.getDateInstance(DateFormat.LONG);
            tvDate.setText(df.format(startDate) + " - " + df.format(endDate));
        } else tvDate.setText(df.format(startDate));
        df = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
        tvTime.setText(df.format(startDate) + " - " + df.format(endDate));
    }

    private void setStatusViewUp() {
        ShushProfile shushProfile = event.getShushProfile();
        if(shushProfile != null) {
            if (shushProfile.getRingerMode() > -1) {
                tvSound.setText(shushProfile.getRingerMode() == AudioManager.RINGER_MODE_SILENT ? "Silent" : "Vibrate");
                soundStatus.setVisibility(View.VISIBLE);
            } else soundStatus.setVisibility(View.GONE);

            if (shushProfile.getBrightness() > 0) {
                tvBrightness.setText(shushProfile.getBrightness() + "%");
                brightnessStatus.setVisibility(View.VISIBLE);
            } else brightnessStatus.setVisibility(View.GONE);

            if (shushProfile.isWifiDisabled()) {
                tvWifi.setText("Disabled");
                wifiStatus.setVisibility(View.VISIBLE);
            } else wifiStatus.setVisibility(View.GONE);
        }
    }

}
