package com.freddieptf.shush.calendar.data;

import android.content.Context;

import com.freddieptf.shush.calendar.data.model.Event;
import com.freddieptf.shush.calendar.data.model.ShushProfile;

import java.util.List;

import rx.Observable;

/**
 * Created by freddieptf on 18/10/16.
 */

public interface EventsDataSource {
    Observable<List<Event>> getEvents(Context context);
    long saveShushProfile(Context context, long eventId, ShushProfile profile);
}
