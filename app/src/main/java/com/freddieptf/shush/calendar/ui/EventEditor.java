package com.freddieptf.shush.calendar.ui;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.Utils.EventShushProfileChangeCallback;
import com.freddieptf.shush.calendar.data.model.Event;
import com.freddieptf.shush.calendar.data.model.ShushProfile;
import com.freddieptf.shush.calendar.data.shush.ShushEventScheduler;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by freddieptf on 2/15/16.
 */
public class EventEditor extends android.support.v4.app.DialogFragment implements EventEditorView {

    @Bind(R.id.textTitle) TextView eventName;
    @Bind(R.id.sectionSound_radioGroup) RadioGroup soundRadioGroup;
    @Bind(R.id.sectionBrightness_seekbar) SeekBar brightnessSeekBar;
    @Bind(R.id.sectionNetwork_switchmobiledata) SwitchCompat mobileDataSwitch;
    @Bind(R.id.sectionNetwork_switchwifi) SwitchCompat wifiSwitch;
    private EventShushProfileChangeCallback eventShushProfileChangeCallback;
    private static final String TAG = "EditEventDialog";
    private EventEditorPresenter presenter;

    @OnClick(R.id.img_nav) void hide(){ dismiss(); }
    @OnClick(R.id.buttonSave) void save(){
        presenter.saveShushProfile(getActivity(), buildProfile());
    }
    @OnClick(R.id.reset) void reset(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        eventShushProfileChangeCallback = (EventShushProfileChangeCallback) getActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void setPresenter(BasePresenter presenter) {
        this.presenter = (EventEditorPresenter) presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(presenter != null) presenter.startLoad();
    }

    @Override
    public void onEventLoad(String name, ShushProfile shushProfile) {
        eventName.setText(name);
        applyShushProfile(shushProfile);
    }

    @Override
    public void onShushProfileSaved(Event updatedEvent, String error) {
        if(updatedEvent != null) {
            eventShushProfileChangeCallback.onProfileChange(updatedEvent);
            ShushEventScheduler.schedule(updatedEvent);
        }else {
            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
        }
    }

    private void applyShushProfile(ShushProfile profile){
        if(profile != null) {
            if (profile.getRingerMode() != -1)
                soundRadioGroup.check(profile.getRingerMode() == 0 ? R.id.radioSilent : R.id.radioVibrate);
            else soundRadioGroup.clearCheck();
            brightnessSeekBar.setProgress(profile.getBrightness());
            wifiSwitch.setChecked(profile.isWifiDisabled());
            mobileDataSwitch.setChecked(profile.isMobileDataDisbaled());
        }
    }

    private ShushProfile buildProfile(){
        ShushProfile.Builder builder = new ShushProfile.Builder();
        builder.setBrightness(brightnessSeekBar.getProgress())
                .setMobileDataDisbaled(mobileDataSwitch.isChecked())
                .setWifiDisabled(wifiSwitch.isChecked())
                .setRingerMode(soundRadioGroup.getCheckedRadioButtonId() == R.id.radioSilent ?
                        AudioManager.RINGER_MODE_SILENT : AudioManager.RINGER_MODE_VIBRATE);
        return builder.build();
    }
}

