package com.freddieptf.shush.calNextras.ui.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.ui.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by freddieptf on 2/24/16.
 */
public class CreateEventChooser extends DialogFragment {

    public final static int TIME = 0;
    public final static int WIFI = 1;
    public final static int LOC = 3;
    public final static String WHAT_KEY = "wuut";
    public final static String WHAT_NAME_KEY = "namewuut";

    CreateEventDialog createEventDialog;
    Bundle b;
    @Bind(R.id.event_title)
    EditText eventTitle;
    @Bind(R.id.rgroup) RadioGroup radioGroup;
    int id;

    private void showDialog(Bundle b) {
        createEventDialog.setArguments(b);
        createEventDialog.show(getActivity().getSupportFragmentManager(), "ed");
        createEventDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
    }


    @OnClick(R.id.bNext) void next(View view){
        if(TextUtils.isEmpty(eventTitle.getText().toString())){
            Snackbar snackbar = Snackbar.make(view, "You need to provide an Event Name", Snackbar.LENGTH_LONG);
            snackbar.show();
        }else {
            switch (id) {
                case R.id.bt_time:
                    b.putInt(WHAT_KEY, TIME);
                    break;
                case R.id.bt_wifi:
                    b.putInt(WHAT_KEY, WIFI);
                    break;
            }
            b.putString(WHAT_NAME_KEY, eventTitle.getText().toString());
            showDialog(b);
            dismiss();
        }
    }

    @OnClick(R.id.bCancel) void cancel(){
        dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_create_event_chooser, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        createEventDialog = new CreateEventDialog();
        b = new Bundle();
        b.putString(MainActivity.TABlE_NAME_KEY, getArguments().getString(MainActivity.TABlE_NAME_KEY));
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                id = checkedId;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
