package com.freddieptf.shush.calNextras.ui.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.freddieptf.shush.calendar.Log;
import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.Utils.AlarmUtils;
import com.freddieptf.shush.calendar.data.DbHelper;
import com.freddieptf.shush.calendar.model.EventModel;
import com.freddieptf.shush.calendar.model.SettingsModel;
import com.freddieptf.shush.calendar.ui.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by freddieptf on 2/23/16.
 */
public class CreateEventDialog extends DialogFragment {

    @Bind(R.id.triggerDate) LinearLayout triggerDateLayout;
    @Bind(R.id.triggerWiFi) LinearLayout triggerWifiLayout;
    @Bind(R.id.textTitle) TextView title;
    @Bind(R.id.sectionSound_radioGroup) RadioGroup soundSettings;
    @Bind(R.id.sectionBrightness_seekbar) SeekBar brightnessSeekBar;
    @Bind(R.id.sectionNetwork_switchmobiledata) SwitchCompat mobileDataSwitch;
    @Bind(R.id.sectionNetwork_switchwifi) SwitchCompat wifiSwitch;
    @Bind(R.id.buttonStartDate) Button buttonStartDate;
    @Bind(R.id.buttonStartTime) Button buttonStartTime;
    @Bind(R.id.buttonStopDate) Button buttonStopDate;
    @Bind(R.id.buttonStopTime) Button buttonStopTime;
    @Bind(R.id.wifiSSID) EditText etWifiSSID;

    public static String EVENT_CREATED = "event_created";

    @OnClick(R.id.img_nav) void hide(){
        dismiss();
    }

    @OnClick(R.id.buttonSave) void save(){
        eventModel.setTitle(eventTitle);
        eventModel.setSettings(settingsModel);
        DbHelper dbHelper = new DbHelper(getActivity());
        long l;
        if(trigger != null && !trigger.isEmpty()) {
            switch (trigger){
                case "time":
                    eventModel.setStart(String.valueOf(start.getTimeInMillis()));
                    eventModel.setEnd(String.valueOf(stop.getTimeInMillis()));
                    l = dbHelper.insertEvent(tableName, eventModel, trigger);
                    eventModel.setId(String.valueOf(l));
                    new AlarmUtils(getActivity(), eventModel).setAlarm();
                    break;
                case "wifi":
                    eventModel.setWifiSSID(etWifiSSID.getText().toString());
                    l = dbHelper.insertEvent(tableName, eventModel, trigger);
                    break;
            }
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(EVENT_CREATED));
            dismiss();
        }
    }

    EventModel eventModel;
    SettingsModel settingsModel;
    String eventTitle, tableName, trigger;
    Calendar start, stop;
    private static final String TAG = "CreateEventDialog";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_create_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        eventTitle = getArguments().getString(CreateEventChooser.WHAT_NAME_KEY);
        tableName = getArguments().getString(MainActivity.TABlE_NAME_KEY);
        Log.d(TAG, eventTitle + " / " + tableName);
        initViews();
        handleSettingsChange();

        if(savedInstanceState != null && savedInstanceState.containsKey("s")){
            settingsModel = savedInstanceState.getParcelable("s");
        }else settingsModel = new SettingsModel();

        eventModel = new EventModel();
        start = Calendar.getInstance();
        stop = Calendar.getInstance();

        if(savedInstanceState != null && savedInstanceState.containsKey("start")){
            start.setTimeInMillis(Long.parseLong(savedInstanceState.getString("start")));
            stop.setTimeInMillis(Long.parseLong(savedInstanceState.getString("stop")));
        }
        dateThings();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("s", settingsModel);
        outState.putString("start", String.valueOf(start.getTimeInMillis()));
        outState.putString("stop", String.valueOf(stop.getTimeInMillis()));
    }

    private void initViews() {
        title.setText(eventTitle);
        switch (getArguments().getInt(CreateEventChooser.WHAT_KEY)){
            case CreateEventChooser.TIME:
                triggerDateLayout.setVisibility(View.VISIBLE);
                trigger = "time";
                break;
            case CreateEventChooser.WIFI:
                triggerWifiLayout.setVisibility(View.VISIBLE);
                trigger = "wifi";
                break;
            case CreateEventChooser.LOC:
                break;
        }
    }

    private void dateThings(){

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM dd yyyy");

        if(start.compareTo(Calendar.getInstance()) != 0){
            buttonStartDate.setText(simpleDateFormat.format(start.getTime()));
            buttonStartTime.setText(new SimpleDateFormat("HH:mm").format(start.getTime()));
            buttonStopDate.setText(simpleDateFormat.format(stop.getTime()));
            buttonStopTime.setText(new SimpleDateFormat("HH:mm").format(stop.getTime()));
        }

        buttonStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        start.set(year, monthOfYear, dayOfMonth);
                        buttonStartDate.setText(simpleDateFormat.format(start.getTime()));
                    }
                }, start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        buttonStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        start.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        start.set(Calendar.MINUTE, minute);
                        buttonStartTime.setText(new SimpleDateFormat("HH:mm").format(start.getTime()));
                    }
                }, start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

        buttonStopDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        stop.set(year, monthOfYear, dayOfMonth);
                        buttonStopDate.setText(simpleDateFormat.format(stop.getTime()));
                    }
                }, start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        buttonStopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        stop.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        stop.set(Calendar.MINUTE, minute);
                        buttonStopTime.setText(new SimpleDateFormat("HH:mm").format(stop.getTime()));
                    }
                }, start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
