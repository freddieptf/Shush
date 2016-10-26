package com.freddieptf.shush.calendar.data.shush;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by freddieptf on 21/10/16.
 */

public class ShushJobCreator implements JobCreator{
    @Override
    public Job create(String tag) {
        if(tag.equals(ShushJob.TAG)){
            return new ShushJob();
        }
        return null;
    }
}
