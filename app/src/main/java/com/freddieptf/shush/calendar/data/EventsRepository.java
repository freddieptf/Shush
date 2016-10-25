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

import rx.Observable;

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
            return Observable.concat(localDataSource.getEvents(context), saveAndGetEvents(context, new DbHelper(context)))
                    .first()
                    .doOnNext(events -> {
                        cache = events;
                        Log.d(TAG, "getEvents: " + (cache == null ? 0 : cache.size()));
                        cacheDirty = false;
                    });
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
        copyCalendarEventstoDb(context, new DbHelper(context));
        cacheDirty = true;
        notifyObservers();
    }

    private Observable<List<Event>> saveAndGetEvents(Context context, DbHelper dbHelper){
        copyCalendarEventstoDb(context, dbHelper);
        return localDataSource.getEvents(context);
    }

    private void copyCalendarEventstoDb(Context context, DbHelper dbHelper) {

        String[] INSTANCE_PROJECTION = new String[] {
                CalendarContract.Instances.EVENT_ID,      // 0
                CalendarContract.Instances.BEGIN,         // 1
                CalendarContract.Instances.END,           // 2
                CalendarContract.Instances.TITLE          // 3
        };

        Calendar currentTime = Calendar.getInstance();
        long startMillis = currentTime.getTimeInMillis();
        currentTime.add(Calendar.DATE, 7); // get the events in the next 7 days
        long endMillis = currentTime.getTimeInMillis();

        String selection = CalendarContract.Instances.EVENT_ID + " = ? ";
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);
        Uri uri = builder.build();

        String[] selectionArgs = new String[1];

        calendarDataSource.getEvents(context)
                .flatMap(Observable::from)
                .doOnCompleted(() -> Log.d(TAG,
                        "copyCalendarEventstoDb: deletedOldEvents: " + deleteOldEvents(dbHelper, startMillis)))
                .doOnNext(event -> {
                    selectionArgs[0] = String.valueOf(event.getId());
                    Cursor cursor = context.getContentResolver().query(uri,
                            INSTANCE_PROJECTION,
                            selection,
                            selectionArgs,
                            null);
                    if(cursor != null && cursor.moveToFirst()){
                        event = new Event(cursor.getLong(0), cursor.getString(3), cursor.getLong(1), cursor.getLong(2));
                        if(!dbHelper.eventExists(String.valueOf(event.getId()))){
                            dbHelper.insertEvent(event);
                            Log.d(TAG, "copyCalendarEventstoDb: inserted: " + event.getName());
                        }
                        else{
                            dbHelper.updateEvent(event);
                            Log.d(TAG, "copyCalendarEventstoDb: updated: " + event.getName());
                        }
                        if(!cursor.isClosed()) cursor.close();
                    }else {
                        dbHelper.deleteEvent(event.getId() + "");
                    }
                })
                .subscribe();
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
