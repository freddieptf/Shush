package com.freddieptf.shush.calendar.data;

import android.content.Context;
import android.database.Cursor;

import com.freddieptf.shush.calendar.data.model.Event;
import com.freddieptf.shush.calendar.data.model.ShushProfile;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.freddieptf.shush.calendar.data.Contract.CalendarEventsTable;

/**
 * Created by freddieptf on 18/10/16.
 */

public class EventsLocalDataSource implements EventsDataSource {

    private final String[] projection = {
            CalendarEventsTable._ID,
            CalendarEventsTable.COLUMN_EVENT_NAME,
            CalendarEventsTable.COLUMN_BEGIN,
            CalendarEventsTable.COLUMN_END,
            CalendarEventsTable.COLUMN_IS_SET
    };

    private final int EVENT_ID = 0;
    private final int EVENT_NAME = 1;
    private final int EVENT_BEGIN = 2;
    private final int EVENT_END = 3;
    private final int EVENT_SHUSH_SET = 4;
    private static final String TAG = "EventsLocalDataSource";

    public EventsLocalDataSource(){}

    @Override
    public Observable<List<Event>> getEvents(Context context) {
        return Observable.fromCallable(() -> {
            DbHelper dbHelper = new DbHelper(context);
            Cursor cursor = dbHelper.query(projection, null, null, CalendarEventsTable.COLUMN_BEGIN + " ASC");

            if (cursor == null || !cursor.moveToFirst()) return new ArrayList<Event>();

            List<Event> events = new ArrayList<>();
            cursor.moveToFirst();
            do {
                Event event = new Event(cursor.getLong(EVENT_ID),
                        cursor.getString(EVENT_NAME),
                        cursor.getLong(EVENT_BEGIN),
                        cursor.getLong(EVENT_END));

                if (cursor.getInt(EVENT_SHUSH_SET) != 0) {
                    event.addShushProfile(dbHelper.getShushProfileIfExists(event.getId()));
                }

                events.add(event);

            } while (cursor.moveToNext());

            if (!cursor.isClosed()) cursor.close();
            return events;
        })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public long saveShushProfile(Context context, long eventId, ShushProfile profile) {
        return new DbHelper(context).insertShushProfile(eventId, profile);
    }
}
