package com.freddieptf.shush.calendar.data;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import com.freddieptf.shush.calendar.data.model.Event;
import com.freddieptf.shush.calendar.data.model.ShushProfile;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by freddieptf on 18/10/16.
 */

public class EventsCalendarDataSource implements EventsDataSource {

    private final String[] EVENTS_PROJECTION = {
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.DURATION,
            CalendarContract.Events.RDATE,
            CalendarContract.Events.DESCRIPTION,
    };

    private final int COLUMN_TITLE = 1;
    private final int COLUMN_START = 2;
    private final int COLUMN_END = 3;
    private final int COLUMN_DURATION = 4;

    public EventsCalendarDataSource(){

    }

    @Override
    public Observable<List<Event>> getEvents(Context context) {
        Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI,
                EVENTS_PROJECTION, null, null, null);

        if(cursor == null || !cursor.moveToFirst()) return Observable.empty();

        List<Event> events = new ArrayList<>();
        cursor.moveToFirst();

        do{
            events.add(new Event(cursor.getLong(0),
                    cursor.getString(COLUMN_TITLE),
                    cursor.getLong(COLUMN_START),
                    cursor.getLong(COLUMN_END)));
        }while (cursor.moveToNext());

        if(!cursor.isClosed()) cursor.close();

        return Observable.just(events);
    }

    @Override
    public long saveShushProfile(Context context, long eventId, ShushProfile profile) {
        return -1;
    }
}
