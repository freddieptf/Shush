package com.freddieptf.shush.calendar.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import com.freddieptf.shush.calendar.data.model.Event;
import com.freddieptf.shush.calendar.data.model.ShushProfile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by freddieptf on 19/10/16.
 */

public class EventsRepository implements EventsDataSource {

    private EventsLocalDataSource localDataSource;
    private EventsCalendarDataSource calendarDataSource;
    private static EventsRepository INSTANCE;
    private static final String TAG = "EventsRepository";
    private List<Event> cache;
    private List<RepositoryObserver> observers = new ArrayList<>();
    private boolean cacheDirty = false;

    private EventsRepository(){
        localDataSource = new EventsLocalDataSource();
        calendarDataSource = new EventsCalendarDataSource();
    }

    public static EventsRepository getInstance(){
        if(INSTANCE == null) INSTANCE = new EventsRepository();
        return INSTANCE;
    }

    @Override
    public Observable<List<Event>> getEvents(Context context) {
        if(cache != null && !cache.isEmpty() && !cacheDirty){
            Log.d(TAG, "getEvents: fetch from cache");
            return Observable.just(cache);
        }else {
            return Observable.concat(localDataSource.getEvents(context),
                    filterAndInsertCalendarEvents(context, new DbHelper(context))
                            .flatMap(new Function<List<Event>, ObservableSource<List<Event>>>() {
                                @Override
                                public ObservableSource<List<Event>> apply(@NonNull List<Event> events) throws Exception {
                                    return localDataSource.getEvents(context);
                                }
                            }))
                    .doOnNext(events -> {
                        cache = events;
                        Log.d(TAG, "getEvents: " + (cache == null ? 0 : cache.size()));
                        cacheDirty = false;
                    })
                    .observeOn(AndroidSchedulers.mainThread());
        }
    }

    @Override
    public long saveShushProfile(Context context, long eventId, ShushProfile profile) {
        long id = localDataSource.saveShushProfile(context, eventId, profile);
        if(id > -1) {
            cacheDirty = true;
            notifyObservers();
        }
        return id;
    }

    public void syncWhenCalendarChange(Context context) {
        filterAndInsertCalendarEvents(context, new DbHelper(context)).subscribe(); // if it crashes, it crashes.
        cacheDirty = true;
        notifyObservers();
    }

    private Observable<List<Event>> filterAndInsertCalendarEvents(Context context, DbHelper dbHelper) {
        return Observable.fromCallable(() -> {
            String[] INSTANCE_PROJECTION = new String[]{
                    CalendarContract.Instances.EVENT_ID,      // 0
                    CalendarContract.Instances.BEGIN,         // 1
                    CalendarContract.Instances.END,           // 2
                    CalendarContract.Instances.TITLE          // 3
            };

            Calendar currentTime = Calendar.getInstance();
            long startMillis = currentTime.getTimeInMillis();
            currentTime.add(Calendar.DATE, 7); // get the events in the next 7 days
            long endMillis = currentTime.getTimeInMillis();

            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, startMillis);
            ContentUris.appendId(builder, endMillis);
            Uri uri = builder.build();

            Cursor cursor = context.getContentResolver().query(uri,
                    INSTANCE_PROJECTION,
                    null, null, null);

            return cursor;
        }).flatMap(new Function<Cursor, ObservableSource<List<Event>>>() {
            @Override
            public ObservableSource<List<Event>> apply(@NonNull Cursor cursor) throws Exception {
                List<Event> events = new ArrayList<Event>();
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Event event = new Event(cursor.getLong(0), cursor.getString(3), cursor.getLong(1), cursor.getLong(2));
                        events.add(event);
                    } while (cursor.moveToNext());
                }
                return Observable.just(events);
//                        return filterCalendarEvents(context, dbHelper, cursor, Calendar.getInstance().getTimeInMillis());
            }
        });

    }

    // FIXME: 5/13/17 this needs rethinking...for now lets trust the event instances table
    private Observable<List<Event>> filterCalendarEvents(Context context, DbHelper dbHelper, Cursor cursor, long startMillis) {
        return calendarDataSource.getEvents(context)
                .concatMap(Observable::fromIterable)
                .flatMap(new Function<Event, ObservableSource<Event>>() {
                    @Override
                    public ObservableSource<Event> apply(@NonNull Event event) throws Exception {
                        return Observable.just(event);
                    }
                })
                .toList()
                .toObservable()
                .doOnComplete(() -> Log.d(TAG,
                        "filterAndInsertCalendarEvents: deletedOldEvents: " + deleteOldEvents(dbHelper, startMillis)));
    }

    private int deleteOldEvents(DbHelper dbHelper, long startMillis){
        Cursor events = dbHelper.query(new String[]{Contract.CalendarEventsTable._ID,
                Contract.CalendarEventsTable.COLUMN_END}, null, null, null);
        int deletes = 0;
        while (events != null && events.moveToNext()){
            if(Long.parseLong(events.getString(1)) < startMillis){
                deletes = dbHelper.deleteEvent(events.getString(0));
            }
        }
        if(events != null && !events.isClosed()) events.close();
        return deletes;
    }

    public void addRepositoryObserver(RepositoryObserver observer){
        if(!observers.contains(observer)) observers.add(observer);
    }

    public void notifyObservers(){
        for(RepositoryObserver observer : observers) observer.notifyDataChange();
    }

    public void removeObserver(RepositoryObserver observer){
        if(observers.contains(observer)) observers.remove(observer);
    }

    public interface RepositoryObserver {
        void notifyDataChange();
    }
}
