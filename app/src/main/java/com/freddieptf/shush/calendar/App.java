package com.freddieptf.shush.calendar;

import android.app.Application;

import com.evernote.android.job.JobManager;
import com.freddieptf.shush.calendar.data.shush.ShushJobCreator;

/**
 * Created by freddieptf on 21/10/16.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JobManager.create(this).addJobCreator(new ShushJobCreator());
    }
}
