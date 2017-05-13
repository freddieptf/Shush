package com.freddieptf.shush.calendar.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.freddieptf.shush.BuildConfig;
import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.data.model.Event;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by freddieptf on 2/15/16.
 */
public class EventActivity extends AppCompatActivity {

    @Bind(R.id.event_title) TextView eventName;
    @Bind(R.id.toolbar) Toolbar toolbar;
    private final String TAG = getClass().getSimpleName();
    public static String EVENT_KEY = "event_keyy";
    private Event event;

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
        setDemColors();
        eventName.setText(event.getName());
    }

    private void setDemColors() {
        int colorInt = getEventColor(event);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp);
        Drawable drawable1 = DrawableCompat.wrap(drawable);
        drawable1.setColorFilter(colorInt, PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(drawable1);
        eventName.setTextColor(colorInt);
    }

    private int getEventColor(Event event){
        switch (event.getColor()){
            default:
                return event.getColor() != -1 ? ContextCompat.getColor(this, event.getColor()) : ContextCompat.getColor(this, R.color.primaryText);
        }
    }


}
