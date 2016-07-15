package com.freddieptf.shush.calendar.ui.views;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.Utils.AlarmUtils;
import com.freddieptf.shush.calendar.Utils.SettingsChangeHelper;
import com.freddieptf.shush.calendar.data.DbHelper;
import com.freddieptf.shush.calendar.model.EventModel;
import com.freddieptf.shush.calendar.model.SettingsModel;
import com.freddieptf.shush.calendar.ui.MainActivity;
import com.freddieptf.shush.calendar.ui.ViewEventActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by freddieptf on 2/15/16.
 */
public class EditEventDialog extends android.support.v4.app.DialogFragment {

    @Bind(R.id.textTitle) TextView tvTitle;
    @Bind(R.id.sectionSound_radioGroup) RadioGroup soundSettings;
    @Bind(R.id.sectionBrightness_seekbar) SeekBar brightnessSeekBar;
    @Bind(R.id.sectionNetwork_switchmobiledata) SwitchCompat mobileDataSwitch;
    @Bind(R.id.sectionNetwork_switchwifi) SwitchCompat wifiSwitch;
    @Bind(R.id.sectionTrigger)
    SectionView triggerView;
    @Bind(R.id.triggerDate) LinearLayout triggerDateView;
    @Bind(R.id.triggerWiFi) LinearLayout triggerWifiView;
    EventModel event;
    SettingsModel settingsModel;
    SettingsChangeHelper settingsChangeHelper;
    AlarmUtils alarmUtils;
    String tableName;

    @OnClick(R.id.img_nav) void hide(){
        dismiss();
    }

    @OnClick(R.id.buttonSave) void save(){
        if(saveSettingsToDb(event, settingsModel) > 0) alarmUtils.setAlarm();
        settingsChangeHelper.onSettingsChange(settingsModel);
        dismiss();
    }

    @OnClick(R.id.reset) void reset(){
        settingsModel = new SettingsModel();
        applySettings(settingsModel);
        alarmUtils.cancelAlarm();
        settingsChangeHelper.onSettingsChange(settingsModel);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        event = getArguments().getParcelable(ViewEventActivity.EVENT_KEY);
        initTriggerView();
        settingsModel = event.getSettings();
        tvTitle.setText(event.getTitle());
        applySettings(settingsModel);
        handleSettingsChange();
        settingsChangeHelper = (SettingsChangeHelper) getActivity();
        alarmUtils = new AlarmUtils(getActivity(), event);
        tableName = getArguments().getString(MainActivity.TABlE_NAME_KEY);
    }

    public void initTriggerView(){
        if(event.getTrigger() != null && !event.getTrigger().isEmpty()){
            triggerView.setVisibility(View.VISIBLE);
            switch (event.getTrigger()){
                case "time":
                    triggerDateView.setVisibility(View.VISIBLE);
                    break;
                case "wifi":
                    triggerWifiView.setVisibility(View.VISIBLE);
                    break;
            }
        }else triggerView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void handleSettingsChange(){
        soundSettings.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(radioGroup.getCheckedRadioButtonId()) {
                    case R.id.radioSilent:
                        settingsModel.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        break;
                    case R.id.radioVibrate:
                        settingsModel.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                        break;
                    default:
                        settingsModel.setRingerMode(-1);
                }

            }
        });

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                settingsModel.setBrightness(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mobileDataSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingsModel.setMobileDataDisabled(b);
            }
        });

        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingsModel.setWifiDisabled(b);
            }
        });
    }


    public void applySettings(SettingsModel settings){
        if(settings.getRingerMode() != -1)
            soundSettings.check(settings.getRingerMode() == 0 ? R.id.radioSilent : R.id.radioVibrate);
        else soundSettings.clearCheck();
        brightnessSeekBar.setProgress(settings.getBrightness());
        wifiSwitch.setChecked(settings.isWifiDisabled());
        mobileDataSwitch.setChecked(settings.isMobileDataDisabled());
    }

    public int saveSettingsToDb(EventModel event, SettingsModel settingsModel){
        SQLiteDatabase database = new DbHelper(getActivity()).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.COLUMN_SILENT_MODE, settingsModel.getRingerMode());
        contentValues.put(DbHelper.COLUMN_SET_BRIGHTNESS, settingsModel.getBrightness());
        contentValues.put(DbHelper.COLUMN_DATA_ENABLED, settingsModel.isMobileDataDisabled());
        contentValues.put(DbHelper.COLUMN_WIFI_ENABLED, settingsModel.isWifiDisabled());

        int i = database.update(tableName != null && !tableName.isEmpty() ? tableName : DbHelper.CALENDAR_EVENTS_TABLE,
                contentValues,
                DbHelper.COLUMN_ID + " = ? ",
                new String[]{event.getId()});
        if (i > 0){
            event.setSettings(settingsModel);
            alarmUtils.setSettingsModel(settingsModel);
        }

        if(database.isOpen()) database.close();
        return i;
    }

}

